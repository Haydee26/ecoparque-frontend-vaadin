# EcoParque – Frontend principal (Vaadin)

Pantalla web principal del **Sistema de gestión del Ecoparque**. Trabajo práctico final de **Programación II** – INSPT-UTN, docente Miguel Silva.

**Autora:** Haydee (trabajo individual).

Necesita el backend funcionando: https://github.com/Haydee26/ecoparque-backend

## Qué ofrece

- Inicio de sesión y menú según el rol (administrador, guía, cuidador)
- Zonas, hábitats (con continentes), especies, itinerarios, guías, cuidadores, asignaciones y responsabilidades
- Solo administrador: usuarios, registro del parque y personal por antigüedad

## Tecnologías

Java, Spring Boot y Vaadin Flow. Se comunica con el backend por REST.

## Cómo ponerlo en marcha

1. Tener el backend corriendo (por defecto en `http://localhost:8080`).
2. Si el backend está en otra computadora, cambiar `backend.url` en `src/main/resources/application.properties` por su IP (por ejemplo `http://192.168.0.8:8080`).
3. Desde esta carpeta ejecutar:

```
mvn spring-boot:run
```

4. Abrir http://localhost:8081 e ingresar.

## Usuarios de prueba

| Usuario | Clave | Rol |
|---|---|---|
| admin | 1234 | Administrador |
| laura | 1111 | Guía |
| mario | 2222 | Cuidador |
