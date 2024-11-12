<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Painel de Salas</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css" integrity="sha384-b6lVK+yci+bfDmaY1u0zE8YYJt0TZxLEAFyYSLHId4xoVvsrQu3INevFKo+Xir8e" crossorigin="anonymous">
        <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
        <link rel="stylesheet" href="styles/index_page.css">
        <link rel="stylesheet" href="styles/card_keys.css">
        <link rel="stylesheet" href="styles/card_reservation.css">
    </head>
    <body>
        <%@ include file="WEB-INF/jspf/header.jspf" %>
        <div id="app" class="container">
            <div v-if="shared.session">
                <div class="page-content"> 
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">Painel de Salas
                        <button class="btn btn-warning btn-sm ms-auto buttons" @click="ChangeScreen()" type="button">
                            <div v-if="isReservations">
                                Voltar
                            </div>
                            <div v-else>
                                Ver Reservas
                            </div>
                        </button>
                    </h2>
                    <div v-if="isReservations === false">
                        <div v-for="location in uniqueLocations" :key="location">
                            <h3>{{ location }}</h3>
                            <div class="row">
                                <div class="col-12 col-sm-6 col-md-4 col-lg-3 col-xl-2 mb-2" 
                                     v-for="room in rooms.filter(room => room.location === location)" 
                                     :key="room.rowid">
                                    <div :class="getRoomStatusClass(room)">
                                        <div class="card-body custom-card-body">
                                            <div class="both-buttons">
                                                <h4 class="card-title">
                                                    <div class="scrolling-container">
                                                        <span 
                                                            :class="{ marquee: room.name.length > 8 }" 
                                                            class="scrolling-text">
                                                            {{ room.name }}
                                                        </span>
                                                    </div>
                                                    <div class="ms-auto button-container">
                                                        <button v-if="room.status !== 'INDISPONIVEL' && room.status !== 'OCUPADO'" 
                                                                class="btn btn-sm buttons custom-btn" 
                                                                type="button" 
                                                                @click="updateInputName(room)" 
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#addKeyModal">
                                                            <i class="bi bi-plus-lg custom-icon"></i>           
                                                        </button>
                                                        <button v-if="shouldShowAddMaterialButton(room.rowid)" class="btn btn-sm buttons custom-btn" 
                                                                type="button" @click="updateInputName(room)"  
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#addMaterialModal">
                                                            <i class="bi bi-inboxes custom-icon"></i>
                                                        </button>
                                                        <button class="btn btn-sm buttons custom-btn" 
                                                                type="button" 
                                                                @click="viewRoom(room)" 
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#editRoomModal">
                                                            <i class="bi bi-info-square custom-icon"></i>
                                                        </button>
                                                    </div>
                                                </h4>
                                            </div>
                                            <div v-if="selectedReservation && selectedReservation.roomid === room.rowid">
                                                <p>Reservado por: {{ selectedReservation.employee }}</p>
                                                <button class="btn btn-sm btn-success" 
                                                        @click="acceptReservation(selectedReservation)">
                                                    <i class="bi bi-check-lg"></i> Aceitar
                                                </button>
                                                <button class="btn btn-sm btn-danger" 
                                                        @click="rejectReservation(selectedReservation)">
                                                    <i class="bi bi-x-lg"></i> Recusar
                                                </button>
                                            </div>

                                            <div v-for="key in getKey(room.rowid)" :key="key.rowid">
                                                <hr>
                                                <div class="d-flex align-items-center both-buttons"> 
                                                    <i class="bi-style-key bi-key-fill"></i>
                                                    <div class="card-text me-2 scrolling-container">
                                                        <span :class="{ marquee: key.employeeName.length > 8 }" class="scrolling-text body">
                                                            {{ key.employeeName || "-" }}
                                                        </span>
                                                    </div>
                                                    <div class="form-check ms-auto">
                                                        <button class="btn btn-success btn-sm ms-auto buttons" 
                                                                type="button" 
                                                                @click="returnKey(key.rowid, key.room, key.employee, key.subject)">
                                                            <i class="bi-style-key bi-key"></i><i class="bi bi-check2"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                            <div v-if="currentMaterial.some(material => material.room === room.rowid)" class="d-flex align-items-center justify-content-center mt-3">
                                                <i 
                                                    class="bi bi-inboxes custom-icon me-2" 
                                                    :title="getMaterialsTooltip(room.rowid)"
                                                    style="font-size:30px;">
                                                </i>
                                                <button 
                                                    class="btn btn-success btn-sm buttons" 
                                                    type="button" 
                                                    @click="returnAllMaterialsForRoom(room.rowid)">
                                                    <i class="bi bi-arrow-90deg-left"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-if="isReservations === true">
                        <div class="row">
                            <div class="col-md-3" style="width: 265px !important;">
                                <input type="text" v-model="searchEmployee" class="form-control" @change="loadReservations()" placeholder="Pesquisar por professor">
                            </div>
                            <div class="col-md-3" style="width: 265px !important;">
                                <input type="text" v-model="searchSubject" class="form-control" @change="loadReservations()" placeholder="Pesquisar por matéria">
                            </div>
                            <div class="col-md-3" style="width: 265px !important;">
                                <input type="date" v-model="searchDate" class="form-control" @change="loadReservations()">
                            </div>
                        </div>
                        <br>
                        <div v-for="(reservationsGroup, roomName) in reservationsGroup" :key="roomName" class="stack-container">
                            <h4 class="card-title">{{ roomName }}</h4>
                            <div class="col-md-12 mb-2">
                                <div v-for="employeeReservations in groupByEmployee(reservationsGroup)" :key="employeeReservations[0].employee" class="card-body custom-card-body card-stack">
                                    <div v-for="(reservation, index) in employeeReservations.reverse().slice(0, 5).reverse()" :key="reservation.rowid" :style="{ top: index * 55 + 'px' }" class="stack-card">
                                        <h5>{{ reservation.employee }} <br> {{ reservation.start.split(' - ')[1].split('/').slice(0, 2).join('/') }}</h5>
                                        <p style="font-size: 14px; margin-left: 15px;">Inicio: {{ reservation.start.split(' - ')[2] }}<br>Terminio: {{ reservation.end.split(' - ')[2] }}<br>Matéria: {{ reservation.subjectName}}</p>
                                    </div>
                                    <div v-if="employeeReservations.length > 5" class="extra-count">
                                        + {{ employeeReservations.length - 5 }} mais
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="addKeyModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5">Adicionar requisitante</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form>
                                <div class="mb-3">
                                    <label for="inputRoom" class="form-label">Sala para retirar chave</label>
                                    <input type="text" v-model="roomName" class="form-control" id="inputRoom" disabled>
                                </div>
                                <div class="mb-3">
                                    <label for="inputEmployee" class="form-label">Funcionario</label>
                                    <select class="form-select" v-model="newEmployee" id="inputEmployee" @change="getSubjects()">
                                        <option v-for="employee in employees" :key="employee.rowid" :value="employee.rowid">{{ employee.name }}</option>
                                    </select>
                                </div>
                                <div v-if="newEmployee !== ''" class="mb-3">
                                    <label for="inputSubject" class="form-label">Materia</label>
                                    <select class="form-select" v-model="newSubject" id="inputSubject">
                                        <option v-if="subjects.length === 0" value="0">Sem matérias para este funcionario</option> 
                                        <option v-for="subject in subjects" :key="subject.subject" :value="subject.subject">{{ subject.subjectName }} - {{ subject.courseName }} - {{ subject.period }}</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <div>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetKey()">Cancelar</button>
                            </div>
                            <div>
                                <button type="button" class="btn btn-primary" :disabled="!newEmployee" data-bs-dismiss="modal" @click="addKey()">Salvar</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="addMaterialModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5">Emprestar materiais</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form>
                                <div class="mb-3">
                                    <label for="inputRoom" class="form-label">Sala para retirar chave</label>
                                    <input type="text" v-model="roomName" class="form-control" id="inputRoom" disabled>
                                </div>
                                <div class="mb-3">
                                    <label for="inputEmployee" class="form-label">Funcionario</label>
                                    <select class="form-select" v-model="newEmployee" id="inputEmployee" @change="getSubjects()">
                                        <option v-for="employee in employees" :key="employee.rowid" :value="employee.rowid">{{ employee.name }}</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="inputMaterial" class="form-label">Material</label>
                                    <select class="form-select" v-model="selectedMaterial" id="inputMaterial">
                                        <option v-for="item in material" :key="item.rowid" :value="item">{{ item.name }}</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <button type="button" class="btn btn-secondary" @click="addToBuffer">Adicionar a lista</button>
                                </div>
                                <div v-if="materialBuffer.length > 0" class="mb-3">
                                    <h5>Materiais Selecionados:</h5>
                                    <ul>
                                        <li v-for="(item, index) in materialBuffer" :key="index">
                                            {{ item.name }}
                                            <button type="button" class="btn-close" @click="removeFromBuffer(index)" aria-label="Remove"></button>
                                        </li>
                                    </ul>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <div>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetMaterial()">Cancelar</button>
                            </div>
                            <div>
                                <button type="button" class="btn btn-primary" :disabled="!newEmployee" data-bs-dismiss="modal" @click="saveMaterials()">Salvar</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="editRoomModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5">{{ roomName }}</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form>
                                <div class="mb-3">
                                    <label for="inputName" class="form-label">Nome da Sala</label>
                                    <input type="text" v-model="roomName" class="form-control" id="inputName"> 
                                </div>
                                <div class="mb-3">
                                    <label for="inputLocation" class="form-label">Localização</label>
                                    <input type="text" v-model="roomLocation" class="form-control" id="inputLocation"> 
                                </div>
                                <div v-if="roomStatus !== 'OCUPADO'" class="mb-3">
                                    <label for="inputStatus" class="form-label">Status</label>
                                    <select class="form-select" v-model="roomStatus" id="inputStatus">
                                        <option value="DISPONIVEL">Disponivel</option>
                                        <option value="INDISPONIVEL">Indisponivel</option>
                                        <option value="LIMPEZA">Limpeza</option>
                                    </select>
                                </div>
                            </form>
                        </div>
                        <hr>
                        <div class="config-container">
                            <a href="rooms.jsp"><i class="config-bi bi bi-gear-fill"></i></a>
                            <div v-for="filters in filters" :key="filters.rowid" class="filter-info">
                                <span class="separator"></span>{{ filters.filterName }} <br> 
                                <span class="description">Descrição: {{ filters.filterDesc || "Não há descrição" }}</span>                            
                            </div>
                        </div>
                        <div class="modal-footer">
                            <div>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm()">Cancelar</button>
                            </div>
                            <div>
                                <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="updateRoom()">Salvar</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="scripts/index.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
