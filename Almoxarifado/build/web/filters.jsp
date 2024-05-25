<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gerenciamento de Filtros de sala</title>
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
                    <h2 class="mb-3 d-flex align-items-center justify-content-between">Filtros de sala
                        <div class="d-flex align-items-center">
                            <input type="text" placeholder="Digite algo..." class="form-control custom-input mx-2"> 
                            <button class="btn btn-success btn-sm ms-auto buttons" @click="resetForm()"  type="button" data-bs-toggle="modal" data-bs-target="#addFilterModal">
                                Adicionar
                            </button>
                            <button type="button" class="btn btn-danger btn-sm ms-1 buttons" @click="deleteUser()">
                                <i class="bi bi-trash-fill"></i>
                            </button>
                        </div>
                    </h2>
                    <table class="table">
                        <tr>
                            <th>NOME</th>
                            <th>DESCRIÇÃO</th>
                            <th>AÇÕES</th>
                        </tr>
                        <tr v-for="item in list" :key="item.rowid">
                            <td>{{ item.type }}</td>
                            <td>{{ item.desc }}</td>
                            <td>
                                <div class="btn-group" role="group" aria-label="Basic Example">
                                    <button type ="button" @click="setVariables(item)" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#addFilterModal"><i class="bi bi-pen"></i></button>
                                    <button type ="button" @click="removeFilter(item.rowid)" class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></button>
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
                </div>
                                    <div class="modal fade" id="addFilterModal" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5">Novo Filtro</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form>
                                        <div class="mb-3">
                                            <label for="inputType" class="form-label">Nome</label>
                                            <input type="text" v-model="newType" class="form-control" id="inputType"> 
                                        </div>
                                        <div class="mb-3">
                                            <label for="inputDesc" class="form-label">Descrição</label>
                                            <input type="textarea" v-model="newDesc" class="form-control" id="inputDesc">
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
            </div>
        </div>

        <script src="scripts/filters.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
