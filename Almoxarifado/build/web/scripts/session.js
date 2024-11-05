const shared = Vue.reactive({session: null});

const session = Vue.createApp({
    data() {
        return{
            shared: shared,
            error: null,
            loginUsername: null,
            loginPassword: null,
            data: null,
            isDarkTheme: false,
            loading: true,
            isOffcanvasOpen: false,
            newLogin: "",
            newName: "",
            newPassword: ""
        }
    },
    computed: {
        // Validação simples do formulário
        isFormValid() {
            return this.newLogin && this.newName;
        }
    },
    methods: {
        // Metodo padrão para realizar solicitação na Api
        async request(url = "", method, data) {
            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {"Content-type": "application/json", },
                    body: JSON.stringify(data)
                });
                if (response.status === 200) {
                    return response.json();
                } else {
                    this.error = response.statusText;
                }
            } catch (e) {
                this.error = e;
                return null;
        }
        },
        loadUserData() {
            if (this.shared.session) {
                this.newLogin = this.shared.session.login;
                this.newName = this.shared.session.name;
            }
        },
        async updateProfile() {
            const data = await this.request(`/Almoxarifado/api/users?id=${this.shared.session.id}`, "PUT", {
                name: this.newName,
                login: this.newLogin,
                role: this.shared.session.role,
            });
            if (data) {
                await this.refreshSession();
                this.resetForm();
            }
        },
        async refreshSession() {
            try {
                const updatedSession = await this.request("/Almoxarifado/api/session", "GET");
                if (updatedSession) {
                    this.shared.session = updatedSession; // Atualiza somente a sessão do usuário logado
                    window.location.reload();
                } else {
                    console.error("Erro ao atualizar a sessão do usuário: resposta da sessão é nula.");
                    this.error = "Sessão não pode ser atualizada. Tente novamente mais tarde.";
                }
            } catch (e) {
                console.error("Erro ao fazer a requisição de sessão:", e);
                this.error = "Erro de rede ou servidor ao atualizar a sessão do usuário.";
            }
        },
        async updatePassword() {
            if (this.newPassword) {
                const data = await this.request(`/Almoxarifado/api/users?id=${this.shared.session.id}`, "PUT", {
                    password: this.newPassword
                });
            } else {
                this.error = "Senha ou ID do usuário não podem estar vazios";
            }
        },
        resetForm() {
            // Reseta os campos do modal quando for cancelado ou fechado
            this.newLogin = "";
            this.newName = "";
            this.newPassword = "";
        },
        toggleMode() {
            this.isDarkTheme = !this.isDarkTheme;
            document.body.classList.toggle('dark-mode', this.isDarkTheme);
            localStorage.setItem('darkMode', JSON.stringify(this.isDarkTheme));

        },

        checkDarkMode() {
            const storedDarkMode = localStorage.getItem('darkMode');
            if (storedDarkMode !== null) {
                this.isDarkTheme = JSON.parse(storedDarkMode);
                document.body.classList.toggle('dark-mode', this.isDarkTheme);
            }
        },

        toggleOffcanvas() {
            this.isOffcanvasOpen = !this.isOffcanvasOpen;
            this.updateOffcanvasState();
        },
        closeOffcanvas() {
            this.isOffcanvasOpen = false;
            this.updateOffcanvasState();
        },
        updateOffcanvasState() {
            const body = document.body;
            if (this.isOffcanvasOpen) {
                body.classList.add('offcanvas-open');
                body.classList.remove('offcanvas-closed');
                body.style.overflow = '';
            } else {
                body.classList.remove('offcanvas-open');
                body.classList.add('offcanvas-closed');
                body.style.overflow = 'auto';
            }
            localStorage.setItem('offcanvasState', JSON.stringify(this.isOffcanvasOpen));
        },
        restoreOffcanvasState() {
            const storedOffcanvasState = localStorage.getItem('offcanvasState');
            if (storedOffcanvasState !== null) {
                this.isOffcanvasOpen = JSON.parse(storedOffcanvasState);
                this.updateOffcanvasState();
            }
        },

        async loadSession() {
            const data = await this.request("/Almoxarifado/api/session", "GET");
            if (data) {
                this.data = data;
                this.error = null;
                this.shared.session = this.data;
            }
            this.loading = false;
        },
        async login() {
            const data = await this.request("/Almoxarifado/api/session", "PUT", {"login": this.loginUsername, "password": this.loginPassword});
            if (data) {
                this.data = data;
                this.error = null;
                this.shared.session = this.data;
                window.location.href = "/Almoxarifado/index.jsp";
            }
        },
        async logout() {
            const data = await this.request("/Almoxarifado/api/session", "DELETE");
            if (data) {
                this.data = data;
                this.error = null;
                this.shared.session = this.data;
                window.location.href = "/Almoxarifado/";
            }
        }
    },
    mounted() {
        this.checkDarkMode();
        this.restoreOffcanvasState();
        this.loadSession();


    }
});
session.mount('#session');

