<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Controle de materiais</title>
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
                        <h2 class="mb-3 d-flex align-items-center justify-content-between">Materiais
                            <div class="d-flex align-items-center">
                                <button class="btn btn-success btn-sm ms-auto buttons" @click="resetForm()"  type="button" data-bs-toggle="modal" data-bs-target="#addMaterialModal">
                                    Adicionar
                                </button>
                            </div>
                        </h2>
                        <div class="row align-items-center">
                            <div class="col-md-2">
                                <label>Qtd. Registros</label>
                                <select class="form-control mb-2" v-model="itemsPerPage" id="registers" @change="reloadPage">
                                    <option value=5>5</option>
                                    <option value=10>10</option>
                                    <option value=20>20</option>
                                    <option value=50>50</option>
                                </select>
                            </div>
                        </div>

                        <table class="table">
                            <tr>
                                <th @click="filterList(1)" style="cursor: pointer;">NOME <i class="bi bi-arrow-down-up"></i></th>
                                <th>AÇÕES</th>
                            </tr>
                            <tr v-for="item in list" :key="item.rowid">
                                <td>{{ item.name }}</td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="Basic Example">
                                        <button type ="button" @click="setVariables(item)" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#addMaterialModal"><i class="bi bi-pen"></i></button>
                                        <button type ="button" @click="removeMaterial(item.rowid)" class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></button>
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
                <div class="modal fade" id="addMaterialModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5">Novo Material</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form>
                                    <div class="mb-3">
                                        <label for="inputName" class="form-label">Nome</label>
                                        <input type="text" v-model="newName" class="form-control" id="inputName"> 
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm()">Cancelar</button>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-primary" :disabled="!newName" data-bs-dismiss="modal" @click="insertOrUpdate()">Salvar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="scripts/material.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
    </body>
</html>
