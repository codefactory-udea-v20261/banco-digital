--
-- PostgreSQL database dump
--

\restrict 7OeZ9Jhbay0dnLGngpavWEkyKe27MIjHFNebAL6l5aPxrgD7qhdVbI8USrycVBj

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: obtener_saldo_total_cliente(uuid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.obtener_saldo_total_cliente(p_cliente_id uuid) RETURNS numeric
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_saldo_total NUMERIC := 0;
BEGIN
    SELECT COALESCE(SUM(saldo), 0) INTO v_saldo_total
    FROM cuenta
    WHERE cliente_id = p_cliente_id
      AND estado = 'ACTIVA';
      
    RETURN v_saldo_total;
END;
$$;


ALTER FUNCTION public.obtener_saldo_total_cliente(p_cliente_id uuid) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: auditoria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auditoria (
    id bigint NOT NULL,
    tabla character varying(50) NOT NULL,
    operacion character varying(10) NOT NULL,
    registro_id character varying(100) NOT NULL,
    datos_antes jsonb,
    datos_despues jsonb,
    usuario_bd character varying(100) DEFAULT CURRENT_USER NOT NULL,
    ip_origen character varying(45),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT auditoria_operacion_check CHECK (((operacion)::text = ANY ((ARRAY['INSERT'::character varying, 'UPDATE'::character varying, 'DELETE'::character varying])::text[])))
);


ALTER TABLE public.auditoria OWNER TO postgres;

--
-- Name: auditoria_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.auditoria_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.auditoria_id_seq OWNER TO postgres;

--
-- Name: auditoria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.auditoria_id_seq OWNED BY public.auditoria.id;


--
-- Name: cliente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cliente (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    numero_cedula character varying(20) NOT NULL,
    primer_nombre character varying(100) NOT NULL,
    segundo_nombre character varying(100),
    primer_apellido character varying(100) NOT NULL,
    segundo_apellido character varying(100),
    email character varying(255) NOT NULL,
    telefono character varying(20),
    fecha_nacimiento date NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by character varying(100) DEFAULT 'SYSTEM'::character varying NOT NULL,
    updated_by character varying(100) DEFAULT 'SYSTEM'::character varying NOT NULL
);


ALTER TABLE public.cliente OWNER TO postgres;

--
-- Name: TABLE cliente; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.cliente IS 'Registro de clientes del banco digital';


--
-- Name: COLUMN cliente.numero_cedula; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.cliente.numero_cedula IS 'Campo inmutable — no puede actualizarse vía API (HU3)';


--
-- Name: cuenta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cuenta (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    numero_cuenta character varying(20) NOT NULL,
    cliente_id uuid NOT NULL,
    tipo_cuenta_id smallint NOT NULL,
    saldo numeric(18,2) DEFAULT 0.00 NOT NULL,
    estado character varying(20) DEFAULT 'ACTIVA'::character varying NOT NULL,
    fecha_apertura date DEFAULT CURRENT_DATE NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by character varying(100) DEFAULT 'SYSTEM'::character varying NOT NULL,
    updated_by character varying(100) DEFAULT 'SYSTEM'::character varying NOT NULL,
    CONSTRAINT cuenta_estado_check CHECK (((estado)::text = ANY ((ARRAY['ACTIVA'::character varying, 'INACTIVA'::character varying, 'BLOQUEADA'::character varying])::text[]))),
    CONSTRAINT cuenta_saldo_check CHECK ((saldo >= (0)::numeric))
);


ALTER TABLE public.cuenta OWNER TO postgres;

--
-- Name: COLUMN cuenta.saldo; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.cuenta.saldo IS 'Saldo transaccional — ADR-001: se gestiona en capa aplicación, no por SP';


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Name: rol; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rol (
    id smallint NOT NULL,
    nombre character varying(50) NOT NULL
);


ALTER TABLE public.rol OWNER TO postgres;

--
-- Name: tipo_cuenta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tipo_cuenta (
    id smallint NOT NULL,
    nombre character varying(50) NOT NULL,
    descripcion character varying(200)
);


ALTER TABLE public.tipo_cuenta OWNER TO postgres;

--
-- Name: tipo_transaccion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tipo_transaccion (
    id smallint NOT NULL,
    nombre character varying(50) NOT NULL
);


ALTER TABLE public.tipo_transaccion OWNER TO postgres;

--
-- Name: token_revocado; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_revocado (
    id bigint NOT NULL,
    jti character varying(100) NOT NULL,
    usuario_id uuid NOT NULL,
    revocado_at timestamp with time zone DEFAULT now() NOT NULL,
    expira_at timestamp with time zone NOT NULL
);


ALTER TABLE public.token_revocado OWNER TO postgres;

--
-- Name: TABLE token_revocado; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.token_revocado IS 'Blacklist de JWT — Sprint 3: verificar en cada request autenticado';


--
-- Name: token_revocado_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.token_revocado_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.token_revocado_id_seq OWNER TO postgres;

--
-- Name: token_revocado_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.token_revocado_id_seq OWNED BY public.token_revocado.id;


--
-- Name: transaccion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transaccion (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    cuenta_origen_id uuid,
    cuenta_destino_id uuid,
    tipo_id smallint NOT NULL,
    monto numeric(18,2) NOT NULL,
    saldo_anterior numeric(18,2) NOT NULL,
    saldo_posterior numeric(18,2) NOT NULL,
    descripcion character varying(255),
    referencia character varying(50),
    estado character varying(20) DEFAULT 'COMPLETADA'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    created_by character varying(100) DEFAULT 'SYSTEM'::character varying NOT NULL,
    CONSTRAINT transaccion_estado_check CHECK (((estado)::text = ANY ((ARRAY['COMPLETADA'::character varying, 'FALLIDA'::character varying, 'REVERTIDA'::character varying])::text[]))),
    CONSTRAINT transaccion_monto_check CHECK ((monto > (0)::numeric))
);


ALTER TABLE public.transaccion OWNER TO postgres;

--
-- Name: TABLE transaccion; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.transaccion IS 'Registro inmutable de operaciones — solo INSERT permitido';


--
-- Name: COLUMN transaccion.saldo_anterior; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.transaccion.saldo_anterior IS 'Snapshot de saldo para trazabilidad total sin recálculo';


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    username character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL,
    cliente_id uuid,
    activo boolean DEFAULT true NOT NULL,
    intentos_fallidos smallint DEFAULT 0 NOT NULL,
    bloqueado_hasta timestamp with time zone,
    ultimo_login timestamp with time zone,
    mfa_secret character varying(100),
    mfa_activo boolean DEFAULT false NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.usuario OWNER TO postgres;

--
-- Name: COLUMN usuario.password_hash; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.usuario.password_hash IS 'BCrypt hash factor 12 — nunca texto plano';


--
-- Name: COLUMN usuario.mfa_secret; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.usuario.mfa_secret IS 'TOTP secret para MFA — Sprint 3';


--
-- Name: usuario_rol; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario_rol (
    usuario_id uuid NOT NULL,
    rol_id smallint NOT NULL
);


ALTER TABLE public.usuario_rol OWNER TO postgres;

--
-- Name: auditoria id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auditoria ALTER COLUMN id SET DEFAULT nextval('public.auditoria_id_seq'::regclass);


--
-- Name: token_revocado id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_revocado ALTER COLUMN id SET DEFAULT nextval('public.token_revocado_id_seq'::regclass);


--
-- Name: auditoria auditoria_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_pkey PRIMARY KEY (id);


--
-- Name: cliente cliente_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_email_key UNIQUE (email);


--
-- Name: cliente cliente_numero_cedula_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_numero_cedula_key UNIQUE (numero_cedula);


--
-- Name: cliente cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_pkey PRIMARY KEY (id);


--
-- Name: cuenta cuenta_numero_cuenta_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT cuenta_numero_cuenta_key UNIQUE (numero_cuenta);


--
-- Name: cuenta cuenta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT cuenta_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: rol rol_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_nombre_key UNIQUE (nombre);


--
-- Name: rol rol_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (id);


--
-- Name: tipo_cuenta tipo_cuenta_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipo_cuenta
    ADD CONSTRAINT tipo_cuenta_nombre_key UNIQUE (nombre);


--
-- Name: tipo_cuenta tipo_cuenta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipo_cuenta
    ADD CONSTRAINT tipo_cuenta_pkey PRIMARY KEY (id);


--
-- Name: tipo_transaccion tipo_transaccion_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipo_transaccion
    ADD CONSTRAINT tipo_transaccion_nombre_key UNIQUE (nombre);


--
-- Name: tipo_transaccion tipo_transaccion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tipo_transaccion
    ADD CONSTRAINT tipo_transaccion_pkey PRIMARY KEY (id);


--
-- Name: token_revocado token_revocado_jti_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_revocado
    ADD CONSTRAINT token_revocado_jti_key UNIQUE (jti);


--
-- Name: token_revocado token_revocado_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_revocado
    ADD CONSTRAINT token_revocado_pkey PRIMARY KEY (id);


--
-- Name: transaccion transaccion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT transaccion_pkey PRIMARY KEY (id);


--
-- Name: transaccion transaccion_referencia_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT transaccion_referencia_key UNIQUE (referencia);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- Name: usuario_rol usuario_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_pkey PRIMARY KEY (usuario_id, rol_id);


--
-- Name: usuario usuario_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_username_key UNIQUE (username);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_auditoria_created_at; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_auditoria_created_at ON public.auditoria USING btree (created_at DESC);


--
-- Name: idx_auditoria_registro; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_auditoria_registro ON public.auditoria USING btree (registro_id);


--
-- Name: idx_auditoria_tabla; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_auditoria_tabla ON public.auditoria USING btree (tabla);


--
-- Name: idx_cliente_activo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cliente_activo ON public.cliente USING btree (activo) WHERE (activo = true);


--
-- Name: idx_cliente_cedula; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cliente_cedula ON public.cliente USING btree (numero_cedula);


--
-- Name: idx_cliente_email; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cliente_email ON public.cliente USING btree (email);


--
-- Name: idx_cuenta_cliente_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cuenta_cliente_id ON public.cuenta USING btree (cliente_id);


--
-- Name: idx_cuenta_estado; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cuenta_estado ON public.cuenta USING btree (estado);


--
-- Name: idx_cuenta_numero; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cuenta_numero ON public.cuenta USING btree (numero_cuenta);


--
-- Name: idx_token_expira; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_token_expira ON public.token_revocado USING btree (expira_at);


--
-- Name: idx_token_jti; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_token_jti ON public.token_revocado USING btree (jti);


--
-- Name: idx_trans_created_at; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_trans_created_at ON public.transaccion USING btree (created_at DESC);


--
-- Name: idx_trans_cuenta_destino; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_trans_cuenta_destino ON public.transaccion USING btree (cuenta_destino_id);


--
-- Name: idx_trans_cuenta_origen; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_trans_cuenta_origen ON public.transaccion USING btree (cuenta_origen_id);


--
-- Name: idx_trans_tipo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_trans_tipo ON public.transaccion USING btree (tipo_id);


--
-- Name: idx_usuario_cliente; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_usuario_cliente ON public.usuario USING btree (cliente_id);


--
-- Name: idx_usuario_rol_rol_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_usuario_rol_rol_id ON public.usuario_rol USING btree (rol_id);


--
-- Name: idx_usuario_username; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_usuario_username ON public.usuario USING btree (username);


--
-- Name: cuenta cuenta_cliente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT cuenta_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES public.cliente(id);


--
-- Name: cuenta cuenta_tipo_cuenta_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT cuenta_tipo_cuenta_id_fkey FOREIGN KEY (tipo_cuenta_id) REFERENCES public.tipo_cuenta(id);


--
-- Name: token_revocado token_revocado_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_revocado
    ADD CONSTRAINT token_revocado_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: transaccion transaccion_cuenta_destino_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT transaccion_cuenta_destino_id_fkey FOREIGN KEY (cuenta_destino_id) REFERENCES public.cuenta(id);


--
-- Name: transaccion transaccion_cuenta_origen_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT transaccion_cuenta_origen_id_fkey FOREIGN KEY (cuenta_origen_id) REFERENCES public.cuenta(id);


--
-- Name: transaccion transaccion_tipo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transaccion
    ADD CONSTRAINT transaccion_tipo_id_fkey FOREIGN KEY (tipo_id) REFERENCES public.tipo_transaccion(id);


--
-- Name: usuario usuario_cliente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES public.cliente(id);


--
-- Name: usuario_rol usuario_rol_rol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_rol_id_fkey FOREIGN KEY (rol_id) REFERENCES public.rol(id);


--
-- Name: usuario_rol usuario_rol_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuario(id) ON DELETE CASCADE;


--
-- Name: TABLE auditoria; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT ON TABLE public.auditoria TO app_user;


--
-- Name: SEQUENCE auditoria_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.auditoria_id_seq TO app_user;


--
-- Name: TABLE cliente; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.cliente TO app_readonly;
GRANT SELECT,INSERT,UPDATE ON TABLE public.cliente TO app_user;


--
-- Name: TABLE cuenta; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.cuenta TO app_readonly;
GRANT SELECT,INSERT,UPDATE ON TABLE public.cuenta TO app_user;


--
-- Name: TABLE tipo_cuenta; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.tipo_cuenta TO app_readonly;
GRANT SELECT ON TABLE public.tipo_cuenta TO app_user;


--
-- Name: TABLE token_revocado; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT ON TABLE public.token_revocado TO app_user;


--
-- Name: SEQUENCE token_revocado_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.token_revocado_id_seq TO app_user;


--
-- Name: TABLE transaccion; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT ON TABLE public.transaccion TO app_user;


--
-- Name: TABLE usuario; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.usuario TO app_user;


--
-- Name: TABLE usuario_rol; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE ON TABLE public.usuario_rol TO app_user;


--
-- PostgreSQL database dump complete
--

\unrestrict 7OeZ9Jhbay0dnLGngpavWEkyKe27MIjHFNebAL6l5aPxrgD7qhdVbI8USrycVBj

