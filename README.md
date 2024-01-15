# Academic API

## :bookmark: **Sumário**
- [Sobre o Projeto](#mortar_board-sobre-o-projeto)
- [Rotas do Projeto](#file_folder-rotas-do-projeto)
- [Tecnologias](#wrench-tecnologias)
   - [Construção da API](#construção-da-api)
   - [Testes](#testes)
   - [IDE, Versionamento e Deploy](#ide-versionamento-e-deploy)
- [Configurações e Instalação](#rocket-configurações-e-instalação)
     - [Requisitos](#requisitos)
- [Licença](#balance_scale-licença)

## :mortar_board: **Sobre o Projeto**

Essa é uma API de um dashboard que pode ser utilizado por alunos de universidades para registrar os dados de seus professores e suas respectivas disciplinas, contabilizando suas faltas e gerenciando suas notas. 

## :file_folder: Rotas do Projeto

### Autenticação

> - **`POST` /auth/register** _Cria um novo usuário com nome, email e senha._
> - **`POST` /auth/login** _Obtém o token do usuário com o email e senha._

### Professores

> - **`GET` /professors/{id}** _Obtém o professor com base no id passado._
> - **`GET` /professors** _Obtém todos os professores do usuário._
> - **`POST` /professors** _Cria um novo professor com nome e email._
> - **`PUT` /professors/{id}** _Atualiza os dados de um professor com base no id passado._
> - **`DELETE` /professors/{id}** _Deleta um professor com base no id passado._

### Disciplinas

> - **`GET` /subjects/{id}** _Obtém a disciplina com base no id passado._
> - **`GET` /subjects** _Obtém todos as disciplinas do usuário._
> - **`POST` /subjects** _Cria uma nova disciplina com nome, descrição e os id's dos professores responsáveis._
> - **`PUT` /subjects/{id}** _Atualiza os dados de uma disciplina com base no id passado._
> - **`PUT` /subjects/{id}/professors** _Atualiza os professores de uma disciplina com base no id passado._
> - **`DELETE` /subjects/{id}** _Deleta uma disciplina com base no id passado._

### Notas

> - **`GET` /grades/{id}** _Obtém a nota com base no id passado._
> - **`GET` /grades** _Obtém todas as notas de uma disciplina._
> - **`POST` /grades** _Cria uma nova nota com nome, valor e o id da disciplina._
> - **`PUT` /grades/{id}** _Atualiza os dados de uma nota com base no id passado._
> - **`DELETE` /grades/{id}** _Deleta uma nota com base no id passado._

### Faltas

> - **`GET` /absences/{id}** _Obtém a falta com base no id passado._
> - **`GET` /absences** _Obtém todas as faltas de uma disciplina._
> - **`POST` /absences** _Cria uma nova falta com data, quantidade e o id da disciplina._
> - **`PUT` /absences/{id}** _Atualiza os dados de uma falta com base no id passado._
> - **`DELETE` /absences/{id}** _Deleta uma falta com base no id passado._

## :wrench: **Tecnologias**

Tecnologias utilizadas no projeto.

### **Construção da API**

- [Java](https://www.java.com/pt-BR/)
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Lombok](https://projectlombok.org)
- [Spring Security](https://spring.io/projects/spring-security/)
- [Java JWT](https://github.com/auth0/java-jwt)
- [Docker](https://www.docker.com)

### **Banco de Dados**

- [PostgreSQL](https://www.postgresql.org)
- [H2](https://www.h2database.com/html/main.html)

### **Testes**

- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org)

### **IDE, Versionamento e Deploy**

- [IntelliJ IDEA](https://www.jetbrains.com/pt-br/idea/)
- [Git](https://git-scm.com)
- [GitHub](https://github.com)
- [Render](https://render.com)
- [Neon](https://neon.tech)

## :rocket: **Configurações e Instalação**

### Requisitos

- [Java/Java SDK](https://jdk.java.net/archive/) e [Maven](https://maven.apache.org) ou [Docker](https://www.docker.com).
- Teste das chamadas realizados com [HTTPie](https://httpie.io/desktop).

### Com o Docker

```sh
docker-compose up
```

### Com o Maven

Obs: você deve conectar o projeto com seu banco de dados PostgreSQL, alterando o arquivo em ```src/main/resources/application.yml```.

```sh
# Primeira compilação do projeto
mvn clean package

# Rodando o projeto
mvn spring-boot:run
```

### Com a IntelliJ IDE

Você pode acessar o arquivo ```AcademicApplication.java``` e executá-lo.

## :balance_scale: **Licença**

Esse projeto está sob a [licença MIT](https://github.com/davsilvam/academic-api/blob/main/LICENSE.md).

---

Feito com 🤍 e ☕ por <a href="https://www.linkedin.com/in/davsilvam/">David Silva</a>.

> [Portfólio](https://davidsilvam.vercel.app) &nbsp;&middot;&nbsp;
> GitHub [@davsilvam](https://github.com/davsilvam) &nbsp;&middot;&nbsp;
> Instagram [@davsilvam_](https://www.instagram.com/davsilvam_/)
