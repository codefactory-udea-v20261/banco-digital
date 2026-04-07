workspace "Banco Digital API" "Arquitectura del sistema Banco Digital" {

    model {
        customer = person "Cliente del Banco" "Un cliente que interactúa con el banco digital a través de la interfaz web." "Customer"
        admin = person "Personal del Banco (Cajero/Admin)" "Empleado del banco que gestiona clientes, cuentas y configuraciones." "Admin"

        bancoSystem = softwareSystem "Banco Digital Backend" "Proporciona la lógica de negocio central, incluyendo gestión de cuentas, clientes y autenticación." {
            
            database = container "Database" "Almacena información de usuarios, clientes, cuentas, transacciones de auditoría y roles." "PostgreSQL" "Database"
            
            apiApp = container "API Application" "Proporciona los endpoints REST usando una Arquitectura Hexagonal (Puertos y Adaptadores)." "Java and Spring Boot" "Spring Boot" {
                # Componentes internos de la API (Arquitectura Hexagonal)
                authModule = component "Auth Module" "Gestiona la identidad, login, y JWT." "Spring Security / JWT"
                accountsModule = component "Accounts Module" "Reglas de negocio para el saldo, bloqueos y validaciones de cuentas." "Java"
                customersModule = component "Customers Module" "Gestión de clientes y validaciones de perfil." "Java"
            }
        }

        # Relaciones Contexto
        customer -> bancoSystem "Consulta saldos y realiza operaciones bancarias vía API" "JSON/HTTPS"
        admin -> bancoSystem "Gestiona usuarios, clientes y configura cuentas vía API" "JSON/HTTPS"
        
        # Relaciones Contenedores
        customer -> apiApp "Hace peticiones a" "JSON/HTTPS"
        admin -> apiApp "Hace peticiones a" "JSON/HTTPS"
        
        apiApp -> database "Lee y escribe datos en" "JDBC/TCP"
    }

    views {
        systemContext bancoSystem "ContextDiagram" "Diagrama de Contexto C4 del Sistema" {
            include *
            autoLayout
        }

        container bancoSystem "ContainerDiagram" "Diagrama de Contenedores C4 del Backend" {
            include *
            autoLayout
        }
        
        component apiApp "ComponentDiagram" "Diagrama de Componentes C4 de la API" {
            include *
            autoLayout
        }

        theme default
        
        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Person" {
                background #08427b
                color #ffffff
                shape Person
            }
            element "Database" {
                shape Cylinder
            }
        }
    }
}