const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newEmployee: '',
            newRoom: '',
            newSubject: "",
            roomName: '',
            roomLocation: '',
            roomStatus: '',
            searchDate: '',
            searchEmployee: '',
            searchSubject: '',
            isReservations: false,
            focusedCard: null,
            room: null,
            selectedReservation: null,
            list: [],
            rooms: [],
            employees: [],
            subjects: [],
            key: [],
            roomFilters: [],
            filters: [],
            reservations: [],
            material: [],
            currentMaterial: [],
            selectedMaterial: null,
            materialBuffer: []
        };
    },
    computed: {
        uniqueLocations() {
            return [...new Set(this.rooms.map(room => room.location))];
        },
        reservationsGroup() {
            return this.groupedReservations();
        }

    },
    methods: {
        getMaterialsTooltip(roomId) {
            return this.currentMaterial
                    .filter(material => material.room === roomId)
                    .map(material => material.materialName)
                    .join(", ");
        },

        // Modifique a lógica de verificação se há materiais na sala
        shouldShowAddMaterialButton(roomId) {
            // Verifica se há materiais presentes na sala
            return !this.currentMaterial.some(material => material.room === roomId);
        },
        addToBuffer() {
            if (this.selectedMaterial) {
                this.materialBuffer.push(this.selectedMaterial);
                this.selectedMaterial = null;
            } else {
                console.warn("Nenhum material selecionado.");
            }
        },
        removeFromBuffer(index) {
            this.materialBuffer.splice(index, 1);
        },
        async saveMaterials() {
            if (this.materialBuffer.length === 0) {
                console.warn("Nenhum material no buffer para salvar.");
                return;
            }

            const currentDateTime = new Date().toISOString();
            for (const item of this.materialBuffer) {
                const response = await this.request("/Almoxarifado/api/current_material", "POST", {
                    employee: this.newEmployee,
                    material: item.rowid,
                    start: currentDateTime,
                    user: this.shared.session.id,
                    room: this.newRoom
                });
                if (!response) {
                    console.error("Erro ao salvar material:", this.error);
                    break;
                }
            }
            this.loadList();
            this.materialBuffer = [];
        },
        resetMaterial() {
            this.newEmployee = "";
            this.materialBuffer = [];
        },
        getActiveReservationForRoom(roomId) {
            const currentDateTime = new Date();
            return this.reservations.find(reservation => {
                // Verifique se a reserva está ativa e pertence à sala específica
                if (reservation.roomid !== roomId || reservation.active !== 1) return false;

                const [startDate, startTime] = reservation.start.split(' - ').slice(1);
                const [endDate, endTime] = reservation.end.split(' - ').slice(1);

                const [startDay, startMonth, startYear] = startDate.split('/').map(Number);
                const [startHour, startMinute] = startTime.split(':').map(Number);
                const startDateTime = new Date(startYear, startMonth - 1, startDay, startHour, startMinute);

                const [endDay, endMonth, endYear] = endDate.split('/').map(Number);
                const [endHour, endMinute] = endTime.split(':').map(Number);
                const endDateTime = new Date(endYear, endMonth - 1, endDay, endHour, endMinute);

                return currentDateTime >= startDateTime && currentDateTime <= endDateTime;
            }) || null; // Retorna `null` se não houver reserva ativa
        },
        
        getRoomStatusClass(room) {
            const activeReservation = this.getActiveReservationForRoom(room.rowid);
            if (activeReservation) {
                return 'custom-reservation';
            } else if (room.status === 'DISPONIVEL') {
                return 'card-available';
            } else {
                return 'card-unavailable';
            }
        },

        async acceptReservation(reservation) {
            const response = await this.request(`/Almoxarifado/api/reservations?id=${reservation.rowid}`, "PUT", {
                active: 0
            });

            if (response) {
                this.newEmployee = reservation.employeeid;
                this.newRoom = reservation.roomid;
                this.newSubject = reservation.subject;

                const existingKey = this.key.find(key => key.room === reservation.roomid);

                if (existingKey) {
                    await this.returnKey(existingKey.rowid, existingKey.room, existingKey.employee, existingKey.subject);
                }

                await this.addKey();

                await this.loadList();

                this.reservations = this.reservations.filter(r => r.rowid !== reservation.rowid);
                this.selectedReservation = null;
            } else {
                console.error("Erro ao aceitar a reserva:", this.error);
            }
        },
        async rejectReservation(reservation) {
            const response = await this.request(`/Almoxarifado/api/reservations?id=${reservation.rowid}`, "PUT", {
                active: 0
            });
            if (response) {
                await this.loadList();

                this.reservations = this.reservations.filter(r => r.rowid !== reservation.rowid);
                this.selectedReservation = null;
            } else {
                console.error("Erro ao recusar a reserva:", this.error);
            }
        },
        groupedReservations() {
            const currentDate = new Date();

            const filteredReservations = this.reservations.filter(reservation => {
                const endDateTimeStr = reservation.end;

                const parts = endDateTimeStr.split(' - ');

                if (parts.length < 3) {
                    console.error("Formato inválido da string de data/hora.");
                    return false;
                }

                const datePart = parts[1];
                const timePart = parts[2];

                const [endDay, endMonth, endYear] = datePart.split('/').map(Number);

                const [endHour, endMinute] = timePart.split(':').map(Number);

                if (isNaN(endDay) || isNaN(endMonth) || isNaN(endYear) || isNaN(endHour) || isNaN(endMinute)) {
                    console.error("Um dos valores é inválido.");
                    return false;
                }

                const endDate = new Date(endYear, endMonth - 1, endDay, endHour || 23, endMinute || 59);

                return currentDate <= endDate;
            });
            const grouped = filteredReservations.reduce((groups, reservation) => {
                if (!groups[reservation.roomName]) {
                    groups[reservation.roomName] = [];
                }
                groups[reservation.roomName].push(reservation);
                return groups;
            }, {});

            Object.keys(grouped).forEach(roomName => {
                grouped[roomName].sort((a, b) => {
                    const [dayA, monthA, yearA] = a.start.split(' - ')[1].split(' ')[0].split('/').map(Number);
                    const timeA = a.start.split(' - ')[1].split(' ')[1] || '00:00';
                    const [hourA, minuteA] = timeA.split(':').map(Number);
                    const dateA = new Date(yearA, monthA - 1, dayA, hourA, minuteA);

                    const [dayB, monthB, yearB] = b.start.split(' - ')[1].split(' ')[0].split('/').map(Number);
                    const timeB = b.start.split(' - ')[1].split(' ')[1] || '00:00';
                    const [hourB, minuteB] = timeB.split(':').map(Number);
                    const dateB = new Date(yearB, monthB - 1, dayB, hourB, minuteB);

                    return dateB - dateA;
                });
            });

            return Object.keys(grouped)
                    .sort((a, b) => a.localeCompare(b))
                    .reduce((sortedGroups, roomName) => {
                        sortedGroups[roomName] = grouped[roomName];
                        return sortedGroups;
                    }, {});
        },
        ChangeScreen() {
            console.log(this.isReservations);
            if(this.isReservations){
                this.loadList();
            } else{
                this.loadReservations();
                console.log("caiu aqui");
            }
            this.isReservations = !this.isReservations;
        },
        groupByEmployee(reservationsGroup) {
            return reservationsGroup.reduce((groups, reservation) => {
                if (!groups[reservation.employee]) {
                    groups[reservation.employee] = [];
                }
                groups[reservation.employee].push(reservation);
                return groups;
            }, {});
        },
        async request(url = "", method, data) {
            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(data)
                });
                if (response.ok) {
                    return response.json();
                } else {
                    this.error = response.statusText;
                }
            } catch (e) {
                this.error = e.message;
            }
            return null;
        },
        async addKey() {
            if (!this.newEmployee) {
                console.error("Um funcionario é necessario para pegar a chave");
                return;
            }
            const currentDateTime = new Date().toISOString();
            const subjectToSend = this.newSubject || 0;
            const data = await this.request("/Almoxarifado/api/keys", "POST", {
                room: this.newRoom,
                employee: this.newEmployee,
                subject: subjectToSend,
                start: currentDateTime,
                user: this.shared.session.id
            });
            if (data) {
                this.loadList();
            }
        },
        async returnKey(id, room, employee, subject) {
            try {
                const data = await this.request(`/Almoxarifado/api/keys?id=${id}&room=${room}&employee=${employee}&subject=${subject}&user=${this.shared.session.id}`, "DELETE");
                if (data) {
                    await this.loadList();
                }
            } catch (error) {
                console.error("Erro ao devolver chave:", error);
            }
        },
        async returnAllMaterialsForRoom(roomId) {
            // Filtra todos os materiais que pertencem à sala
            const materialsInRoom = this.currentMaterial.filter(mat => mat.room === roomId);

            if (materialsInRoom.length > 0) {
                // Chama a função returnMaterial para cada material encontrado na sala
                for (const material of materialsInRoom) {
                    await this.returnMaterial(material.rowid, material.room, material.employee);
                }
            } else {
                console.warn("Nenhum material encontrado para esta sala.");
            }
        },

        async returnMaterial(id, room, employee) {
            try {
                const material = this.currentMaterial.find(mat => mat.rowid === id && mat.room === room && mat.employee === employee);

                const data = await this.request(
                        `/Almoxarifado/api/current_material?id=${material.rowid}&employee=${material.employee}&material=${material.material}&user=${this.shared.session.id}`,
                        "DELETE"
                        );

                if (data) {
                    await this.loadList(); // Recarrega a lista para atualizar o estado
                }
            } catch (error) {
                console.error("Erro ao devolver material:", error);
            }
        },
        async updateRoom() {
            const index = this.list.findIndex(item => item.rowid === this.room.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.roomName,
                    location: this.roomLocation,
                    status: this.roomStatus
                };
            }
            const data = await this.request(`/Almoxarifado/api/rooms?id=` + (this.room.rowid), "PUT", {
                name: this.roomName,
                location: this.roomLocation,
                status: this.roomStatus
            });
            this.loadList();
            this.resetForm();
            this.room = null;
        },
        async loadList() {
            const dataR = await this.request(`/Almoxarifado/api/rooms`, "GET");
            if (dataR) {
                this.rooms = dataR.list.map(room => ({
                        ...room,
                        returnKey: false
                    }));
            }
            const dataE = await this.request(`/Almoxarifado/api/employees`, "GET");
            if (dataE) {
                this.employees = dataE.list;
            }
            const dataK = await this.request(`/Almoxarifado/api/keys`, "GET");
            if (dataK) {
                this.key = dataK.list;
            }
            const dataRF = await this.request(`/Almoxarifado/api/filters_room?column=${0}&sort=${0}`, "GET");
            if (dataRF) {
                this.roomFilters = dataRF.list;
            }
            const dataM = await this.request(`/Almoxarifado/api/material`, "GET");
            if (dataM) {
                this.material = dataM.list;
            }
            const dataC = await this.request(`/Almoxarifado/api/current_material`, "GET");
            if (dataC) {
                this.currentMaterial = dataC.list;
            }
            this.loadReservationsToday();
        },
        async loadReservations(){
            const dataRE = await this.request(`/Almoxarifado/api/reservations?employee=${this.searchEmployee}&subject=${this.searchSubject}&date=${this.searchDate}&search=${1}`, "GET");
            if (dataRE) {
                this.reservations = dataRE.list;
            }
        },
        async loadReservationsToday(){
            const dataRE = await this.request(`/Almoxarifado/api/reservations?today=${1}`, "GET");
            if (dataRE) {
                this.reservations = dataRE.list;
            }
        },
        async getSubjects() {
            const dataS = await this.request(`/Almoxarifado/api/employee_subject`, "GET");
            if (dataS) {
                this.subjects = dataS.list.filter(subject => subject.employee === this.newEmployee);
            }
        },
        getRoomFilters(roomId) {
            this.filters = this.roomFilters.filter(filter => filter.roomid === roomId);
        },
        getKey(id) {
            return this.key.filter(filter => filter.room === id);
        },
        viewRoom(room) {
            this.room = room;
            this.roomid = room.rowid;
            this.roomName = room.name;
            this.roomLocation = room.location;
            this.roomStatus = room.status;
            this.getRoomFilters(room.rowid);
        },
        updateInputName(room) {
            this.roomName = room.name;
            this.newRoom = room.rowid;
        },
        resetKey() {
            this.newRoom = '';
            this.newEmployee = '';
            this.roomName = '';
        },
        resetForm() {
            this.room = null;
            this.roomName = '';
            this.roomLocation = '';
            this.roomStatus = '';
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
