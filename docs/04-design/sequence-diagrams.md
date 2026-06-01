# Диаграммы последовательности

---

## Сценарий 1: Вход в систему (UC-02)

```plantuml
@startuml
title Вход в систему — POST /auth/login

actor "Пользователь" as User
participant "LoginPage\n(React)" as UI
participant "AuthController\n(Control)" as C
participant "AuthService\n(Mediator)" as M
participant "UserRepository\n(Foundation)" as F
database "PostgreSQL" as DB

User -> UI : Ввод username + password
UI -> C : POST /auth/login\n{LoginRequestDTO}
C -> M : login(loginRequestDTO)
M -> F : findByUsername(username)
F -> DB : SELECT * FROM app_user WHERE username=?
DB --> F : User
F --> M : Optional<User>
M -> M : passwordEncoder.matches(password, user.password)
alt Пароль верный
    M -> M : generateJWT(user)
    M --> C : LoginResponseDTO {token}
    C --> UI : HTTP 200 {token}
    UI -> UI : Сохранить токен в UserContext
    UI --> User : Перенаправить на главную
else Пароль неверный
    M --> C : BadCredentialsException
    C --> UI : HTTP 401 Unauthorized
    UI --> User : Сообщение об ошибке
end
@enduml
```

---

## Сценарий 2: Создание задачи (UC-25)

```plantuml
@startuml
title Создание задачи — POST /api/projects/boards/{boardId}/tasks

actor "Участник" as User
participant "ProjectPage\n(React)" as UI
participant "TaskController\n(Control)" as C
participant "TaskService\n(Mediator)" as M
participant "KanbanBoardRepo\n(Foundation)" as BR
participant "KanbanTaskRepo\n(Foundation)" as TR
database "PostgreSQL" as DB

User -> UI : Нажать «Добавить задачу»,\nввести title + description
UI -> C : POST /api/projects/boards/{boardId}/tasks\nAuthorization: Bearer <JWT>\n{TaskCreateDTO}
C -> C : @Valid проверить TaskCreateDTO
C -> M : createTask(boardId, dto)
M -> BR : findById(boardId)
BR -> DB : SELECT * FROM kanban_board WHERE id=?
DB --> BR : KanbanBoard
BR --> M : Optional<KanbanBoard>
alt Доска найдена
    M -> M : KanbanTask.build(title, desc, TODO, board)
    M -> TR : save(task)
    TR -> DB : INSERT INTO kanban_task ...
    DB --> TR : KanbanTask (с id)
    TR --> M : KanbanTask
    M -> M : modelMapper.map(task, TaskResponseDTO)
    M --> C : TaskResponseDTO
    C --> UI : HTTP 200 {TaskResponseDTO}
    UI --> User : Задача появляется на доске
else Доска не найдена
    M --> C : ResourceNotFoundException
    C --> UI : HTTP 404 Not Found
end
@enduml
```

---

## Сценарий 3: Добавление сотрудника в компанию (UC-13)

```plantuml
@startuml
title Добавление сотрудника — POST /api/company/workers

actor "Admin" as Admin
participant "CompanyPage\n(React)" as UI
participant "CompanyController\n(Control)" as C
participant "CompanyService\n(Mediator)" as M
participant "UserRepository\n(Foundation)" as UR
database "PostgreSQL" as DB

Admin -> UI : Ввести email сотрудника,\nнажать «Добавить»
UI -> C : POST /api/company/workers\nAuthorization: Bearer <JWT>\n{WorkerRequestDTO}
C -> M : addWorkerToMyCompany(dto)
M -> M : getCurrentUser() — определить компанию Admin
M -> UR : findByEmail(dto.email)
UR -> DB : SELECT * FROM app_user WHERE email=?
DB --> UR : User
UR --> M : Optional<User>
alt Пользователь найден
    M -> M : worker.setCompany(adminCompany)
    M -> UR : save(worker)
    UR -> DB : UPDATE app_user SET company_id=? WHERE id=?
    DB --> UR : OK
    C --> UI : HTTP 204 No Content
    UI --> Admin : Список сотрудников обновлён
else Пользователь не найден
    M --> C : ResourceNotFoundException
    C --> UI : HTTP 404 Not Found
end
@enduml
```

---

## Сценарий 4: Вход через Google (OAuth2)

```plantuml
@startuml
title OAuth2 Google Login

actor "Пользователь" as User
participant "React SPA" as UI
participant "Google OAuth2" as Google
participant "Spring Security\nOAuth2" as SS
participant "OAuth2successHandler\n(Security)" as Handler
participant "UserRepository\n(Foundation)" as UR
database "PostgreSQL" as DB

User -> UI : Нажать «Войти через Google»
UI -> SS : GET /oauth2/authorization/google
SS -> Google : Redirect с client_id + scope
Google --> User : Страница согласия Google
User -> Google : Подтвердить
Google -> SS : callback с authorization code
SS -> Google : Обмен code на access_token
Google --> SS : access_token + email + name
SS -> Handler : onAuthenticationSuccess(email, name)
Handler -> UR : findByEmail(email)
UR -> DB : SELECT * FROM app_user WHERE email=?
DB --> UR : Optional<User>
alt Пользователь существует
    Handler -> Handler : generateJWT(existingUser)
else Новый пользователь
    Handler -> UR : save(newUser)
    UR -> DB : INSERT INTO app_user ...
    Handler -> Handler : generateJWT(newUser)
end
Handler --> UI : Redirect с JWT-токеном
UI -> UI : Сохранить токен (OAuthRedirect)
UI --> User : Перенаправить на главную
@enduml
```
