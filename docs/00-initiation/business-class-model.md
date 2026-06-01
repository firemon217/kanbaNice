# Модель бизнес-классов (высокоуровневая)

## Диаграмма

```plantuml
@startuml
title Модель бизнес-классов kanbaNice

class Company {
  name: String
  --
  createProject()
  addEmployee()
  removeEmployee()
}

class User {
  name: String
  email: String
  role: RoleType
  --
  login()
  updateProfile()
  resetPassword()
}

class KanbanProject {
  name: String
  createdAt: DateTime
  --
  addMember()
  createBoard()
  delete()
}

class ProjectMember {
  role: ProjectMemberRole
}

class KanbanBoard {
  name: String
  createdAt: DateTime
  --
  createTask()
  rename()
  delete()
}

class KanbanTask {
  title: String
  description: String
  status: TaskStatus
  --
  updateStatus()
  edit()
  delete()
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

Company "1" *-- "0..*" User : содержит
Company "1" *-- "0..*" KanbanProject : владеет
KanbanProject "1" *-- "1..*" KanbanBoard : содержит
KanbanProject "1" *-- "0..*" ProjectMember : имеет
KanbanBoard "1" *-- "0..*" KanbanTask : содержит
ProjectMember "0..*" --> "1" User : ссылается на
User --> RoleType : имеет роль
ProjectMember --> ProjectMemberRole : имеет роль
KanbanTask --> TaskStatus : имеет статус
@enduml
```

## Описание классов

| Класс | Ответственность |
|-------|----------------|
| **Company** | Корневая организация; владеет пользователями и проектами |
| **User** | Участник системы с ролью и учётными данными |
| **KanbanProject** | Рабочая область с досками и командой |
| **ProjectMember** | Связь пользователя с проектом и ролью внутри него |
| **KanbanBoard** | Канбан-доска, хранящая задачи |
| **KanbanTask** | Атомарная единица работы со статусом |
