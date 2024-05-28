<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Historico de salas</title>
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
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">Historico de chaves</h2>
                    <label for="registers" class="form-label"></label>
                    <select class="mb-3" v-model="itemsPerPage" id="registers" @change="reloadPage">
                        <option value=5>5</option>
                        <option value=10>10</option>
                        <option value=20>20</option>
                        <option value=50>50</option>
                    </select>
                    <table class="table">
                        <tr>
                            <th @click="filterList(1)" style="cursor: pointer;">DATA <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterList(2)" style="cursor: pointer;">FUNCIONARIO <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterList(3)" style="cursor: pointer;">MATERIA <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterList(4)" style="cursor: pointer;">CURSO <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterList(5)" style="cursor: pointer;">SALA <i class="bi bi-arrow-down-up"></i></th>
                            <th @click="filterList(6)" style="cursor: pointer;">TIPO <i class="bi bi-arrow-down-up"></i></th>
                        </tr>
                        <tr v-for="item in list" :key="item.rowid">
                            <td> {{ item.date }} </td>
                            <td> {{ item.employeeName }} </td>
                            <td> {{ item.subjectName }} </td>
                            <td> {{ item.courseName }} </td>
                            <td> {{ item.roomName }} </td>
                            <td> {{ item.type }} </td>
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
            </div>
        </div>

        <script src="scripts/history.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
