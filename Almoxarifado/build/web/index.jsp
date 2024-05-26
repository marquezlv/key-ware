<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Painel de salas</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css" integrity="sha384-b6lVK+yci+bfDmaY1u0zE8YYJt0TZxLEAFyYSLHId4xoVvsrQu3INevFKo+Xir8e" crossorigin="anonymous">
        <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
        <link rel="stylesheet" href="styles/index_page.css">
    </head>
    <body>
        <%@ include file="WEB-INF/jspf/header.jspf" %>
        <div id="app" class="container">
            <div v-if="shared.session">
                <div class="page-content">                  
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">Painel de salas</h2>
                    <div v-for="location in uniqueLocations" :key="location">
                        <h3>{{ location }}</h3>
                        <div class="row">
                            <div class="col-md-2 mb-2 d-flex justify-content-center" v-for="room in rooms.filter(room => room.location === location)" :key="room.rowid">
                                <div :class="room.status === 'DISPONIVEL' ? 'card-available' : 'card-unavailable'">
                                    <div class="card-body custom-card-body">
                                        <h4 class="card-title">
                                            {{ room.name }} 
                                            <button v-if="room.status !== 'INDISPONIVEL' && room.status !== 'OCUPADO'" class="btn btn-success btn-sm ms-auto buttons" type="button" @click="updateInputName(room)" data-bs-toggle="modal" data-bs-target="#addKeyModal">
                                                <i class="bi bi-plus-circle"></i>
                                            </button>
                                            <button class="btn btn-warning btn-sm ms-auto buttons" type="button" @click="viewRoom(room)" data-bs-toggle="modal" data-bs-target="#editRoomModal">
                                                <i class="bi bi-info-square"></i>
                                            </button>
                                        </h4>
                                        <div v-for="key in getKey(room.rowid)" :key="key.rowid">
                                            <hr>
                                            <div class="card-text"><i class="bi bi-key-fill"></i> {{ key.employeeName || "-" }}</div>
                                            <br>
                                            <div class="form-check">
                                                <button class="btn btn-success btn-sm ms-auto buttons" type='button' @click='returnKey(key.rowid, key.room, key.employee)'><i class="bi bi-key"></i><i class="bi bi-check2"></i></button>
                                            </div>
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
                                        <select class="form-select" v-model="newEmployee" id="inputEmployee">
                                            <option v-for="employee in employees" :key="employee.rowid" :value="employee.rowid">{{ employee.name }}</option>
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
                                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="addKey()">Salvar</button>
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
                                    <div class="mb-3">
                                        <label for="inputStatus" class="form-label">Status</label>
                                        <select class="form-select" v-model="roomStatus" id="inputStatus">
                                            <option value="DISPONIVEL">Disponivel</option>
                                            <option value="INDISPONIVEL">Indisponivel</option>
                                            <option value="LIMPEZA">Limpeza</option>
                                        </select>
                                    </div>
                                </form>
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
        </div>

    <script src="scripts/index.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
</body>
</html>
