<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Funcionários</title>
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
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">Funcionários
                        <div class="d-flex align-items-center"> 
                            <button class="btn btn-success btn-sm ms-auto buttons" @click="resetForm()" type="button" data-bs-toggle="modal" data-bs-target="#addEmployeeModal">
                                Adicionar
                            </button>
                        </div>
                    </h2>
                    <label for="registers" class="form-label"></label>
                    <select class="mb-3" v-model="itemsPerPage" id="registers" @change="reloadPage">
                        <option value=5>5</option>
                        <option value=10>10</option>
                        <option value=20>20</option>
                        <option value=50>50</option>
                    </select>
                    <table class="table">
                        <tr>
                            <th @click="filterList(1)" style="cursor: pointer;">NOME <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterList(2)" style="cursor: pointer;">FUNÇÃO <i class="bi bi-arrow-down-up"></i></th>
                            <th>MATÉRIA LECIONADA</th>
                            <th>AÇÕES</th>
                        </tr>
                        <tr v-for="item in list" :key="item.rowid">
                            <td>{{ item.name}}</td>
                            <td>{{ item.type }}</td>
                            <td>
                                <div v-for="item2 in getEmployeesSubjects(item.rowid)" :key="item2.rowid" class="filter-container">
                                    {{ item2.subjectName }} - {{ item2.subjectPeriod }} - {{ item2.courseName}}
                                    <button class="btn btn-danger btn-sm buttons" type="button" @click="removeSubject(item2.rowid)" type="button"><i class="bi bi-trash"></i></button>
                                </div>
                                <button class="btn btn-success btn-sm ms-auto buttons" type="button" @click="updateInputName(item)" type="button" data-bs-toggle="modal" data-bs-target="#addSubjectModal"><i class="bi bi-plus-lg"></i></button>
                            </td>



                            <td>
                                <div class="btn-group" role="group" aria-label="Basic Example">
                                    <button type ="button" @click="setVariables(item)" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#addEmployeeModal"><i class="bi bi-pen"></i></button>
                                    <button type ="button" @click="removeEmployee(item.rowid)" class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                        </tr>
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
                <div class="modal fade" id="addEmployeeModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 v-if="employee === null" class="modal-title fs-5">Novo Funcionário</h1>
                                <h1 v-else class="modal-title fs-5">Editar Funcionário</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form>
                                    <div class="mb-3">
                                        <label for="inputName" class="form-label">Nome</label>
                                        <input type="text" v-model="newName" class="form-control" id="inputName" required> 
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputRole" class="form-label">Função</label>
                                        <select class="form-select" v-model="newType">
                                            <option value="PROFESSOR">PROFESSOR</option>
                                            <option value="FUNCIONARIO">FUNCIONÁRIO</option>
                                            <option value="ESTAGIARIO">ESTAGIÁRIO</option>
                                            <option value="MONITOR">MONITOR</option>
                                        </select>
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm()">Cancelar</button>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-primary"  :disabled="!isFormValid" data-bs-dismiss="modal" @click="insertOrUpdate()">Salvar</button>
                                </div>
                            </div>
                        </div> 
                    </div>
                </div>
                <div class="modal fade" id="addSubjectModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5">Adicionar Matéria</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form>
                                    <div class="mb-3">
                                        <label for="inputEmployee" class="form-label">Matéria para o Professor</label>
                                        <input type="text" v-model="employeeName" class="form-control" id="inputEmployee" disabled>
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputSubject" class="form-label">Matéria</label>
                                        <select class="form-select" v-model="newSubject" id="inputSubject">
                                            <option v-for="item3 in subjects" :key="item3.rowid" :value="item3.rowid">{{ item3.name }} - {{ item3.period }} - {{ item3.courseName }}</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">

                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm2()">Cancelar</button>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-primary" :disabled="!newSubject" data-bs-dismiss="modal" @click="addEmployeeSubject()">Salvar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="scripts/employees.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
