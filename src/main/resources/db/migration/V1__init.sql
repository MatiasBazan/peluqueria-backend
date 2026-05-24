-- Esquema inicial del sistema de turnos de peluqueria.
-- Tipos generados a partir del modelo JPA (Hibernate 6.5 / MySQLDialect) para que el
-- esquema coincida con spring.jpa.hibernate.ddl-auto=validate.

create table usuarios (
    id       bigint       not null auto_increment,
    nombre   varchar(80)  not null,
    email    varchar(120) not null,
    password varchar(255) not null,
    rol      enum ('ADMIN') not null,
    primary key (id),
    constraint uk_usuarios_email unique (email)
) engine=InnoDB;

create table servicios (
    id               bigint        not null auto_increment,
    nombre           varchar(120)  not null,
    descripcion      varchar(500),
    duracion_minutos integer       not null,
    precio           decimal(10,2) not null,
    activo           bit           not null,
    primary key (id)
) engine=InnoDB;

create table profesionales (
    id        bigint      not null auto_increment,
    nombre    varchar(80) not null,
    apellido  varchar(80) not null,
    telefono  varchar(30),
    foto      varchar(500),
    activo    bit         not null,
    primary key (id)
) engine=InnoDB;

create table clientes (
    id       bigint       not null auto_increment,
    nombre   varchar(120) not null,
    telefono varchar(30)  not null,
    email    varchar(120),
    primary key (id),
    constraint uk_clientes_telefono unique (telefono)
) engine=InnoDB;

create table horarios_laborales (
    id             bigint  not null auto_increment,
    profesional_id bigint  not null,
    dia_semana     enum ('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') not null,
    hora_inicio    time(6) not null,
    hora_fin       time(6) not null,
    primary key (id),
    constraint fk_horario_profesional foreign key (profesional_id) references profesionales (id)
) engine=InnoDB;

create table profesional_servicio (
    profesional_id bigint not null,
    servicio_id    bigint not null,
    primary key (profesional_id, servicio_id),
    constraint fk_profserv_profesional foreign key (profesional_id) references profesionales (id),
    constraint fk_profserv_servicio    foreign key (servicio_id)    references servicios (id)
) engine=InnoDB;

create table turnos (
    id                 bigint       not null auto_increment,
    cliente_id         bigint       not null,
    profesional_id     bigint       not null,
    servicio_id        bigint       not null,
    fecha_hora         datetime(6)  not null,
    estado             enum ('CANCELADO','COMPLETADO','CONFIRMADO','PENDIENTE') not null,
    codigo_cancelacion varchar(12)  not null,
    created_at         datetime(6)  not null,
    primary key (id),
    constraint fk_turno_cliente     foreign key (cliente_id)     references clientes (id),
    constraint fk_turno_profesional foreign key (profesional_id) references profesionales (id),
    constraint fk_turno_servicio    foreign key (servicio_id)    references servicios (id)
) engine=InnoDB;

create index idx_turno_prof_fecha on turnos (profesional_id, fecha_hora);
create index idx_turno_cliente    on turnos (cliente_id);
