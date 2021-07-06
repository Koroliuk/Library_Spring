SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;


CREATE TABLE public.book
(
    id               bigint         NOT NULL,
    amount           integer        NOT NULL,
    price            numeric(19, 2) NOT NULL,
    publication_date date           NOT NULL
);

ALTER TABLE public.book
    OWNER TO library;

CREATE SEQUENCE public.book_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.book_id_seq
    OWNER TO library;
ALTER SEQUENCE public.book_id_seq OWNED BY public.book.id;


CREATE TABLE public.book_translate
(
    id               bigint                  NOT NULL,
    authors_string   character varying(200)  NOT NULL,
    description      character varying(1000) NOT NULL,
    edition_name     character varying(50)   NOT NULL,
    language_of_book character varying(30)   NOT NULL,
    title            character varying(100)  NOT NULL,
    book_id          bigint                  NOT NULL,
    language_id      bigint                  NOT NULL
);

ALTER TABLE public.book_translate
    OWNER TO library;

CREATE SEQUENCE public.book_translate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.book_translate_id_seq
    OWNER TO library;

ALTER SEQUENCE public.book_translate_id_seq OWNED BY public.book_translate.id;


CREATE TABLE public.language
(
    id   bigint               NOT NULL,
    name character varying(2) NOT NULL
);

ALTER TABLE public.language
    OWNER TO library;

CREATE SEQUENCE public.language_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.language_id_seq
    OWNER TO library;

ALTER SEQUENCE public.language_id_seq OWNED BY public.language.id;


CREATE TABLE public.orders
(
    id           bigint                NOT NULL,
    end_date     date                  NOT NULL,
    order_status character varying(30) NOT NULL,
    start_date   date                  NOT NULL,
    book_id      bigint                NOT NULL,
    user_id      bigint                NOT NULL
);

ALTER TABLE public.orders
    OWNER TO library;

CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.orders_id_seq
    OWNER TO library;

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


CREATE TABLE public.users
(
    id         bigint                 NOT NULL,
    is_blocked boolean                NOT NULL,
    login      character varying(20)  NOT NULL,
    password   character varying(255) NOT NULL,
    role       character varying(20)  NOT NULL
);

ALTER TABLE public.users
    OWNER TO library;

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.users_id_seq
    OWNER TO library;

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


ALTER TABLE ONLY public.book
    ALTER COLUMN id SET DEFAULT nextval('public.book_id_seq'::regclass);

ALTER TABLE ONLY public.book_translate
    ALTER COLUMN id SET DEFAULT nextval('public.book_translate_id_seq'::regclass);

ALTER TABLE ONLY public.language
    ALTER COLUMN id SET DEFAULT nextval('public.language_id_seq'::regclass);

ALTER TABLE ONLY public.orders
    ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);

ALTER TABLE ONLY public.users
    ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


ALTER TABLE ONLY public.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.book_translate
    ADD CONSTRAINT book_translate_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.language
    ADD CONSTRAINT language_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.language
    ADD CONSTRAINT uk_g8hr207ijpxlwu10pewyo65gv UNIQUE (name);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_ow0gan20590jrb00upg3va2fn UNIQUE (login);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk32ql8ubntj5uh44ph9659tiih FOREIGN KEY (user_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.book_translate
    ADD CONSTRAINT fkbpyto459pyjr36gj3518lw1l9 FOREIGN KEY (book_id) REFERENCES public.book (id);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fkgfhb3qthqm0ds4n4o0yrgy9mj FOREIGN KEY (book_id) REFERENCES public.book (id);

ALTER TABLE ONLY public.book_translate
    ADD CONSTRAINT fkkjesg0w93vgooy33wpsuuosyf FOREIGN KEY (language_id) REFERENCES public.language (id);
