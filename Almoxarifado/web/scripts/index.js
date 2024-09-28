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
            list: [],
            rooms: [],
            employees: [],
            subjects: [],
            key: [],
            roomFilters: [],
            filters: [],
            reservations: []
        };
    },
    computed: {
        uniqueLocations() {
            return [...new Set(this.rooms.map(room => room.location))];
        },
        reservationsGroup() {
            return this.groupedReservations(); // Call the method to return grouped reservations
        }

    },
    methods: {
        groupedReservations() {
            const currentDate = new Date(); // Current date and time

            const filteredReservations = this.reservations.filter(reservation => {
                // Process the 'end' field from the reservation  
                const endDateTimeStr = reservation.end;
                console.log("String de data/hora:", endDateTimeStr);

                // Separar a string e ignorar o primeiro elemento (dia da semana)
                const parts = endDateTimeStr.split(' - ');

                // Verifique se há pelo menos 3 partes (dia da semana, data, hora)
                if (parts.length < 3) {
                    console.error("Formato inválido da string de data/hora.");
                    return false; // Retorna falso se o formato não estiver correto
                }

                // A data e a hora são as partes seguintes
                const datePart = parts[1]; // 12/09/2024
                const timePart = parts[2];  // 20:23

                // Processar a parte da data
                const [endDay, endMonth, endYear] = datePart.split('/').map(Number);
                console.log(`Dia: ${endDay}, Mês: ${endMonth}, Ano: ${endYear}`);

                // Processar a parte da hora
                const [endHour, endMinute] = timePart.split(':').map(Number);
                console.log(`Hora: ${endHour}, Minuto: ${endMinute}`);

                // Verifique se os valores extraídos são válidos
                if (isNaN(endDay) || isNaN(endMonth) || isNaN(endYear) || isNaN(endHour) || isNaN(endMinute)) {
                    console.error("Um dos valores é inválido.");
                    return false;
                }

                // Criar o objeto de data
                const endDate = new Date(endYear, endMonth - 1, endDay, endHour || 23, endMinute || 59);
                console.log("Data de término:", endDate, " - ", currentDate);

                // Apenas retorna reservas onde a data e hora 'end' é maior que o tempo atual
                return currentDate <= endDate;
            });
            // Grouping reservations by room
            const grouped = filteredReservations.reduce((groups, reservation) => {
                if (!groups[reservation.roomName]) {
                    groups[reservation.roomName] = [];
                }
                groups[reservation.roomName].push(reservation);
                return groups;
            }, {});

            // Sorting reservations by date (in descending order, latest first)
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

            // Return the grouped reservations, sorted alphabetically by room name
            return Object.keys(grouped)
                    .sort((a, b) => a.localeCompare(b))
                    .reduce((sortedGroups, roomName) => {
                        sortedGroups[roomName] = grouped[roomName];
                        return sortedGroups;
                    }, {});
        },
        ChangeScreen() {
            this.isReservations = !this.isReservations;
            console.log(this.isReservations);
        },
        groupByEmployee(reservationsGroup) {
            return reservationsGroup.reduce((groups, reservation) => {
                // Agrupa reservas por nome do funcionário
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
            const dataRE = await this.request(`/Almoxarifado/api/reservations`, "GET");
            if (dataRE) {
                this.reservations = dataRE.list;
            }
            console.log(this.reservations);

        },
        async loadReservation() {
            search = "true";
            console.log(this.searchDate);
            const dataRE = await this.request(`/Almoxarifado/api/reservations?employee=${this.searchEmployee}&subject=${this.searchSubject}&date=${this.searchDate}&search=${search}`, "GET");
            if (dataRE) {
                this.reservations = dataRE.list;
            }
            console.log(this.reservations);
        },
        async getSubjects() {
            const dataS = await this.request(`/Almoxarifado/api/employee_subject`, "GET");
            if (dataS) {
                this.subjects = dataS.list.filter(subject => subject.employee === this.newEmployee);
            }
            console.log(this.subjects);
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
