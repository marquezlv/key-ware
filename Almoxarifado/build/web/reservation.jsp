<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Reservas de salas</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css" integrity="sha384-b6lVK+yci+bfDmaY1u0zE8YYJt0TZxLEAFyYSLHId4xoVvsrQu3INevFKo+Xir8e" crossorigin="anonymous">
        <script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
        <link rel="stylesheet" href="styles/page.css">
    </head>
    <body>
        <%@ include file="WEB-INF/jspf/header.jspf" %>
        <div id="app" class="container">
            <div v-if="shared.session">
                <div v-if="error" class="alert alert-danger m-2" role="alert">
                    {{ error }}
                </div>
                <div v-else class="normal-page">
                    <div class="wrapper">
                        <h2 class="mb-3 d-flex align-items-center justify-content-between">Reservas de Salas
                            <div class="d-flex align-items-center">
                                <button class="btn btn-success btn-sm ms-auto buttons" @click="resetForm()" type="button" data-bs-toggle="modal" data-bs-target="#addReservModal">
                                    Adicionar
                                </button>
                            </div>
                        </h2>
                        <div>
                            <select class="mb-3 me-3" v-model="itemsPerPage" id="registers" @change="reloadPage">
                                <option value=5>5</option>
                                <option value=10>10</option>
                                <option value=20>20</option>
                                <option value=50>50</option>
                            </select>
                            <select class="mb-3 me-3 w-25" v-model="listReservation" id="filter-reservation" @change="reloadPage">
                                <option value=1>Mostrar Futuras</option>
                                <option value=2>Mostrar Anteriores</option>
                            </select>
                        </div>
                        <table class="table">
                            <tr class="tr-style">
                                <th @click="filterList(1)" style="cursor: pointer;">FUNCIONÁRIO <i class="bi bi-arrow-down-up"></i></th>
                                <th @click="filterList(2)" style="cursor: pointer;">MATÉRIA  <i class="bi bi-arrow-down-up"></i></th> 
                                <th @click="filterList(3)" style="cursor: pointer;">SALA <i class="bi bi-arrow-down-up"></i></th>
                                <th @click="filterList(4)" style="cursor: pointer;">LOCAL <i class="bi bi-arrow-down-up"></i></th>
                                <th @click="filterList(5)" style="cursor: pointer;">INÍCIO <i class="bi bi-arrow-down-up"></i></th>
                                <th @click="filterList(6)" style="cursor: pointer;">TÉRMINO  <i class="bi bi-arrow-down-up"></i></th>
                                <th>AÇÕES</th>
                            </tr>
                            <tr v-for="item in list" :key="item.rowid">
                                <td>{{ item.employee }}</td>
                                <td>{{ item.subjectName || '-' }}</td>
                                <td>{{ item.roomName }}</td>
                                <td>{{ item.location }}</td>
                                <td>{{ item.start }}</td>
                                <td>{{ item.end }}</td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="Basic Example">
                                        <button type ="button" @click="setVariables(item)" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#addReservModal"><i class="bi bi-pen"></i></button>
                                        <button type ="button" @click="removeReservation(item.rowid)" class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></button>
                                    </div>
                                </td>
                        </table>
                        <div class="pagination-container">
                            <div class="pagination">
                                <button @click="previousPage" :disabled="currentPage === 1">Anterior</button>
                                <div v-if="totalPages > 1">
                                    <span v-for="page in pagination()" :key="page">
                                        <button v-if="page === 'prevJump'" @click="jumpPages(-5)">←</button>
                                        <button v-else-if="page === 'nextJump'" @click="jumpPages(5)">→</button>
                                        <button v-else @click="goToPage(page)" :class="{ 'active': page === currentPage }">{{ page }}</button>
                                    </span>
                                </div>
                                <button @click="nextPage" :disabled="currentPage === totalPages">Próxima</button>
                            </div>
                        </div>
                        <br>
                    </div>
                </div>
                <div class="modal fade" id="addReservModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5">Novo requisitante</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form>
                                    <div class="mb-3">
                                        <label for="inputEmployee" class="form-label">Funcionário</label>
                                        <select class="form-select" v-model="newEmployee" id="inputEmployee" @change="getSubjects()">
                                            <option v-for="item2 in employees" :key="item2.rowid" :value="item2.rowid">{{ item2.name }}</option>
                                        </select>
                                    </div>
                                    <div v-if='newEmployee !== ""' class="mb-3">
                                        <label for="inputSubject" class="form-label">Matéria</label>
                                        <select class="form-select" v-model="newSubject" id="inputSubject">
                                            <option v-if="subjects.length === 0" value="0">Este funcionário não possui matérias</option>
                                            <option v-for="item4 in subjects" :key="item4.subject" :value="item4.subject">{{ item4.subjectName }} - {{ item4.subjectPeriod }} - {{ item4.courseName }}</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputRoom" class="form-label">Sala</label>
                                        <select class="form-select" v-model="newRoom" id="inputRoom" @change="updateInputLocation">
                                            <option v-for="item3 in rooms" :key="item3.rowid" :value="item3.rowid">{{ item3.name }}</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputLocation" class="form-label">Localização</label>
                                        <input type="text" v-model="newLocation" class="form-control" id="inputLocation" disabled> 
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputDate" class="form-label">Data e Horário de Início</label>
                                        <input type="datetime-local" v-model="newDate" class="form-control" id="inputDate" :min="minDate"> 
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputEnd" class="form-label">Data e Horário de Término</label>
                                        <input type="datetime-local" v-model="newEnd" class="form-control" id="inputEnd" :min="newDate || minDate"> 
                                    </div>
                                </form>

                                <div class="mb-3 form-check">
                                    <input type="checkbox" v-model="isRecurring" class="form-check-input" id="recurringCheck">
                                    <label class="form-check-label" for="recurringCheck">Recorrente</label>
                                </div>

                                <div v-if="isRecurring" class="mb-3">
                                    <label for="inputWeeks" class="form-label">Quantas semanas? (52 semanas = semestre todo)</label>
                                    <select v-model="recurringWeeks" class="form-control" id="inputWeeks">
                                        <option v-for="n in 52" :key="n" :value="n">{{ n }}</option>
                                    </select>
                                </div>

                            </div>
                            <div class="modal-footer">
                                <div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm()">Cancelar</button>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-primary" :disabled='!isFormValid' data-bs-dismiss="modal" @click="insertOrUpdate()">Salvar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="scripts/reservation.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
