# Domain Model (концептуальная модель)

## Диаграмма

```plantuml
@startuml
title Domain Model — kanbaNice

class User {
  id: Long
  name: String
  username: String
  email: String
  password: String
  userType: UserType
  providerType: AuthProviderType
  providerId: String
  roles: Set<RoleType>
  pendingEmail: String
  resetToken: String
  resetTokenExpiry: LocalDateTime
}

class Company {
  id: Long
  name: String
}

class KanbanProject {
  id: Long
  name: String
  createdAt: LocalDateTime
}

class ProjectMember {
  id: Long
  role: ProjectMemberRole
}

class KanbanBoard {
  id: Long
  name: String
  createdAt: LocalDateTime
}

class KanbanTask {
  id: Long
  title: String
  description: String
  status: TaskStatus
  statusChangedBy: String
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}

enum RoleType {
  ADMIN
  USER
  AUDITOR
}

enum ProjectMemberRole {
  LEADER
  WORKER
}

enum TaskStatus {
  TODO
  DONE
}

enum AuthProviderType {
  LOCAL
  GOOGLE
}

' Relationships
Company "1" -- "0..*" User : employs >
Company "1" -- "0..*" KanbanProject : owns >

KanbanProject "1" -- "0..*" ProjectMember : has >
KanbanProject "1" -- "0..*" KanbanBoard : contains >

KanbanBoard "1" -- "0..*" KanbanTask : holds >

ProjectMember "0..*" -- "1" User : references >

User --> RoleType
ProjectMember --> ProjectMemberRole
KanbanTask --> TaskStatus
User --> AuthProviderType
@enduml
```

## Концептуальные связи

| Связь | Тип | Мощность | Описание |
|-------|-----|----------|----------|
| Company → User | Агрегация | 1 : 0..* | Компания объединяет пользователей |
| Company → KanbanProject | Композиция | 1 : 0..* | Проекты принадлежат компании |
| KanbanProject → KanbanBoard | Композиция | 1 : 1..* | Проект содержит доски |
| KanbanProject → ProjectMember | Композиция | 1 : 0..* | Проект имеет участников |
| KanbanBoard → KanbanTask | Композиция | 1 : 0..* | Доска содержит задачи |
| ProjectMember → User | Ассоциация | 0..* : 1 | Участник ссылается на пользователя |
