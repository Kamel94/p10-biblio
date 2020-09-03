DROP SEQUENCE IF EXISTS public.pret_id_seq CASCADE;
CREATE SEQUENCE public.pret_id_seq;

DROP TABLE IF EXISTS public.pret CASCADE;
CREATE TABLE public.pret (
    id BIGINT NOT NULL DEFAULT nextval('public.pret_id_seq'),
    date_pret TIMESTAMP NOT NULL,
    date_retour TIMESTAMP NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    exemplaire_id BIGINT NOT NULL,
    statut VARCHAR(5) NOT NULL,
    prolongation INTEGER NOT NULL,
    CONSTRAINT pret_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.pret_id_seq OWNED BY public.pret.id;

DROP SEQUENCE IF EXISTS public.reservation_id_seq CASCADE;
CREATE SEQUENCE public.reservation_id_seq;

DROP TABLE IF EXISTS public.reservation CASCADE;
CREATE TABLE public.reservation (
    id BIGINT NOT NULL DEFAULT nextval('public.reservation_id_seq'),
    booking TIMESTAMP NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    exemplaire_id BIGINT NOT NULL,
    statut VARCHAR(15) NOT NULL,
    notification_date TIMESTAMP,
    CONSTRAINT reservation_pk PRIMARY KEY (id)
);

ALTER SEQUENCE public.reservation_id_seq OWNED BY public.reservation.id;