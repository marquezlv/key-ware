 <%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Matérias e Cursos</title>
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
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">
                        Materias
                        <div class="d-flex align-items-center">
                            <button class="btn btn-success btn-sm ms-auto buttons" @click="resetForm()" type="button" data-bs-toggle="modal" data-bs-target="#addSubjectModal">
                                Adicionar
                            </button>
                        </div>
                    </h2>
                    <label for="registers" class="form-label"></label>
                    <select class="mb-3" v-model="itemsPerPageSub" id="registers" @change="reloadPageSub">
                        <option value=5>5</option>
                        <option value=10>10</option>
                        <option value=20>20</option>
                        <option value=50>50</option>
                    </select>

                    <table class="table">
                        <tr>
                            <th @click="filterListSub(1)" style="cursor: pointer;">NOME <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterListSub(2)" style="cursor: pointer;">CURSO <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterListSub(3)" style="cursor: pointer;">PERIODO <i class="bi bi-arrow-down-up"></i></th>
                            <th>AÇÕES</th>
                        </tr>
                        <tr v-for="item in list" :key="item.rowid">
                            <td>{{ item.name}}</td>
                            <td>{{ item.courseName }}</td>
                            <td>{{ item.period }}</td>
                            <td>
                                <div class="btn-group" role="group" aria-label="Basic Example">
                                    <button type ="button" @click="setVariables(item)" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#addSubjectModal"><i class="bi bi-pen"></i></button>
                                    <button type ="button" @click="removeSubject(item.rowid)" class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                    </table>
                    <div class="pagination-container">
                        <div class="pagination">
                            <button @click="previousPageSub" :disabled="currentPageSub === 1">Anterior</button>
                            <div v-if="totalPagesSub > 1">
                                <span v-for="page in paginationSub()" :key="page">
                                    <button v-if="page === 'prevJump'" @click="jumpPagesSub(-5)">←</button>
                                    <button v-else-if="page === 'nextJump'" @click="jumpPagesSub(5)">→</button>
                                    <button v-else @click="goToPageSub(page)" :class="{ 'active': page === currentPageSub }">{{ page }}</button>
                                </span>
                            </div>
                            <button @click="nextPageSub" :disabled="currentPageSub === totalPagesSub">Próxima</button>
                        </div>
                    </div>
                    <br>
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">
                        Cursos
                        <div class="d-flex align-items-center">
                            <button class="btn btn-success btn-sm ms-auto buttons" @click="resetForm()" type="button" data-bs-toggle="modal" data-bs-target="#addCourseModal">
                                Adicionar
                            </button>
                        </div>
                    </h2>
                    <label for="registers" class="form-label"></label>
                    <select class="mb-3" v-model="itemsPerPageCourse" id="registers" @change="reloadPageCourse">
                        <option value=5>5</option>
                        <option value=10>10</option>
                        <option value=20>20</option>
                        <option value=50>50</option>
                    </select>
                    <table class="table">
                        <tr>
                            <th @click="filterListCourse(1)" style="cursor: pointer;">NOME <i class="bi bi-arrow-down-up"></i></th>
                            <th>AÇÕES</th>
                        </tr>
                        <tr v-for="item3 in course" :key="item3.rowid">
                            <td>{{ item3.name}}</td>
                            <td>
                                <div class="btn-group" role="group" aria-label="Basic Example">
                                    <button type ="button" @click="setVariablesCourse(item3)" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#addCourseModal"><i class="bi bi-pen"></i></button>
                                    <button type ="button" @click="removeCourse(item3.rowid)" class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></button>
                                </div>
                            </td>
                    </table>
                    <div class="pagination-container">
                        <div class="pagination">
                            <button @click="previousPageCourse" :disabled="currentPageCourse === 1">Anterior</button>
                            <div v-if="totalPagesCourse > 1">
                                <span v-for="page in paginationCourse()" :key="page">
                                    <button v-if="page === 'prevJump'" @click="jumpPagesCourse(-5)">←</button>
                                    <button v-else-if="page === 'nextJump'" @click="jumpPagesCourse(5)">→</button>
                                    <button v-else @click="goToPageCourse(page)" :class="{ 'active': page === currentPageCourse }">{{ page }}</button>
                                </span>
                            </div>
                            <button @click="nextPageCourse" :disabled="currentPageCourse === totalPagesCourse">Próxima</button>
                        </div>
                    </div>
                    <br>
                </div>
                <div class="modal fade" id="addSubjectModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5">Nova materia</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form>
                                    <div class="mb-3">
                                        <label for="inputName" class="form-label">Nome</label>
                                        <input type="text" v-model="newName" class="form-control" id="inputName" required> 
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputCourse" class="form-label">Curso</label>
                                        <select class="form-select" v-model="newCourse" id="course" required>
                                            <option v-for="item2 in course" :key="item2.rowid" :value="item2.rowid">{{ item2.name }}</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="inputCourse" class="form-label">Periodo</label>
                                        <select class="form-select" v-model="newPeriod" id="period" required>
                                            <option value="MATUTINO">Matutino</option>
                                            <option value="VESPERTINO">Vespertino</option>
                                            <option value="NOTURNO">Noturno</option>
                                        </select>
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm()">Cancelar</button>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="insertOrUpdate()">Salvar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal fade" id="addCourseModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5">Novo Curso</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form>
                                    <div class="mb-3">
                                        <label for="inputCourse" class="form-label">Nome</label>
                                        <input type="text" v-model="CourseAdd" class="form-control" id="inputCourse" required> 
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm()">Cancelar</button>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="insertOrUpdateCourse()">Salvar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                </div> 
            </div>

        <script src="scripts/subjects.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
