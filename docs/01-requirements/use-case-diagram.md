# Use Case Диаграмма (системные прецеденты)

## Диаграмма

```plantuml
@startuml
title Системные прецеденты kanbaNice

left to right direction

actor "Anonymous" as Anon
actor "User (USER)" as UserActor
actor "Admin (ADMIN)" as AdminActor
actor "Auditor (AUDITOR)" as AuditorActor

rectangle "kanbaNice System" {

    package "Аутентификация" {
        usecase "UC-01 Регистрация" as UC01
        usecase "UC-02 Вход (email/пароль)" as UC02
        usecase "UC-03 Вход через Google (OAuth2)" as UC03
        usecase "UC-04 Запрос сброса пароля" as UC04
        usecase "UC-05 Сброс пароля по токену" as UC05
    }

    package "Профиль пользователя" {
        usecase "UC-06 Просмотр профиля" as UC06
        usecase "UC-07 Редактирование профиля" as UC07
        usecase "UC-08 Удаление аккаунта" as UC08
    }

    package "Компания" {
        usecase "UC-09 Создать компанию" as UC09
        usecase "UC-10 Просмотр компании" as UC10
        usecase "UC-11 Обновить компанию" as UC11
        usecase "UC-12 Удалить компанию" as UC12
        usecase "UC-13 Добавить сотрудника" as UC13
        usecase "UC-14 Удалить сотрудника" as UC14
    }

    package "Проекты" {
        usecase "UC-15 Список проектов" as UC15
        usecase "UC-16 Создать проект" as UC16
        usecase "UC-17 Просмотр проекта" as UC17
        usecase "UC-18 Удалить проект" as UC18
        usecase "UC-19 Добавить участника" as UC19
    }

    package "Доски" {
        usecase "UC-20 Список досок" as UC20
        usecase "UC-21 Создать доску" as UC21
        usecase "UC-22 Обновить доску" as UC22
        usecase "UC-23 Удалить доску" as UC23
    }

    package "Задачи" {
        usecase "UC-24 Список задач" as UC24
        usecase "UC-25 Создать задачу" as UC25
        usecase "UC-26 Просмотр задачи" as UC26
        usecase "UC-27 Обновить задачу" as UC27
        usecase "UC-28 Удалить задачу" as UC28
    }
}

Anon --> UC01
Anon --> UC02
Anon --> UC03
Anon --> UC04
Anon --> UC05

UserActor --> UC06
UserActor --> UC07
UserActor --> UC08
UserActor --> UC15
UserActor --> UC17
UserActor --> UC20
UserActor --> UC24
UserActor --> UC25
UserActor --> UC26
UserActor --> UC27

AdminActor --> UC09
AdminActor --> UC10
AdminActor --> UC11
AdminActor --> UC12
AdminActor --> UC13
AdminActor --> UC14
AdminActor --> UC16
AdminActor --> UC18
AdminActor --> UC19
AdminActor --> UC21
AdminActor --> UC22
AdminActor --> UC23
AdminActor --> UC28

AuditorActor --> UC06
AuditorActor --> UC10
AuditorActor --> UC15
AuditorActor --> UC17
AuditorActor --> UC20
AuditorActor --> UC24
AuditorActor --> UC26
@enduml
```

## Сводная таблица

| Код | Прецедент | Акторы | Метод API |
|-----|-----------|--------|-----------|
| UC-01 | Регистрация | Anonymous | POST /auth/signup |
| UC-02 | Вход по паролю | Anonymous | POST /auth/login |
| UC-03 | Вход через Google | Anonymous | OAuth2 redirect |
| UC-04 | Запрос сброса пароля | Anonymous | POST /api/users/forgot-password |
| UC-05 | Сброс пароля | Anonymous | POST /api/users/reset-password |
| UC-06 | Просмотр профиля | User, Auditor | GET /api/users/profile |
| UC-07 | Редактирование профиля | User | PUT /api/users/profile |
| UC-08 | Удаление аккаунта | User | DELETE /api/users/profile |
| UC-09 | Создать компанию | Admin | POST /api/company |
| UC-10 | Просмотр компании | Admin, Auditor | GET /api/company |
| UC-11 | Обновить компанию | Admin | PUT /api/company |
| UC-12 | Удалить компанию | Admin | DELETE /api/company |
| UC-13 | Добавить сотрудника | Admin | POST /api/company/workers |
| UC-14 | Удалить сотрудника | Admin | DELETE /api/company/workers/{id} |
| UC-15 | Список проектов | User, Auditor | GET /api/projects |
| UC-16 | Создать проект | Admin | POST /api/projects |
| UC-17 | Просмотр проекта | User, Auditor | GET /api/projects/{id} |
| UC-18 | Удалить проект | Admin | DELETE /api/projects/{id} |
| UC-19 | Добавить участника | Admin | POST /api/projects/{id}/members/{userId} |
| UC-20 | Список досок | User, Auditor | GET /api/projects/{id}/boards |
| UC-21 | Создать доску | Admin | POST /api/projects/{id}/boards |
| UC-22 | Обновить доску | Admin | PUT /api/projects/boards/{boardId} |
| UC-23 | Удалить доску | Admin | DELETE /api/projects/boards/{boardId} |
| UC-24 | Список задач | User, Auditor | GET /api/projects/boards/{boardId}/tasks |
| UC-25 | Создать задачу | User | POST /api/projects/boards/{boardId}/tasks |
| UC-26 | Просмотр задачи | User, Auditor | GET /api/projects/tasks/{taskId} |
| UC-27 | Обновить задачу | User | PUT /api/projects/tasks/{taskId} |
| UC-28 | Удалить задачу | Admin | DELETE /api/projects/tasks/{taskId} |
