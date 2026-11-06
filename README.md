# EcoParque – Frontend principal (Vaadin)

Pantalla web principal del **Sistema de gestión del Ecoparque**. Trabajo práctico final de **Programación II** – INSPT-UTN, docente Miguel Silva.

**Autora:** Haydee (trabajo individual).

Necesita el backend funcionando: https://github.com/Haydee26/ecoparque-backend

## Qué ofrece

- Inicio de sesión. El menú (barra superior y lateral) muestra solo las secciones que el rol del usuario puede ver, y los botones Cargar, Modificar y Eliminar aparecen solo donde puede editar. Si alguien escribe a mano la dirección de una pantalla no permitida, vuelve al inicio.
- Zonas, hábitats (con continentes), especies, itinerarios, empleados, guías, cuidadores, asignaciones y responsabilidades.
- **Zonas con minimapa:** al cargar o modificar una zona se muestra el terreno del parque (cuadrícula de 10 por 6) con los lugares ocupados y los libres. Se toca un casillero libre para elegir el lugar; un rectángulo verde indica que entra y uno rojo que se pisa con otra zona. Tiene botones para buscar un lugar libre o dejar la zona sin ubicar.
- **Mapa del parque** con las zonas dibujadas y el detalle de cada una.
- **Salud de las especies:** en Especies, quien tiene permiso de Salud (cuidador, veterinario, administrador) ve el estado de cada especie y puede **avisar que está enferma**, escribiendo qué le pasa. La pantalla **Especies enfermas** (para administrador y veterinario) lista los avisos con la especie, la zona, qué le pasa, quién avisó y la fecha, y permite corregir el aviso o marcarla sana.
- **Roles:** pantalla del administrador con una matriz de casillas para crear roles nuevos y elegir qué puede Ver y Editar en cada sección.
- **Usuarios:** alta, baja y modificación, eligiendo el rol entre los que existen en el sistema (incluidos los creados por el administrador).
- Registro del parque y personal por antigüedad.

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

Si el puerto 8081 está ocupado por una ejecución anterior, cerrar ese proceso antes de volver a iniciar.

## Usuarios de prueba

| Usuario | Clave | Rol |
|---|---|---|
| admin | 1234 | Administrador |
| laura | 1111 | Guía |
| mario | 2222 | Cuidador |
| invitado | 3333 | Invitado |
| vet | 4444 | Veterinario |
