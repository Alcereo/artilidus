CREATE TABLE public.articles (
  uid UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
  id INTEGER NOT NULL DEFAULT nextval('articles_id_seq'::regclass),
  title TEXT NOT NULL,
  contenthtml TEXT NOT NULL,
  contentmarkdown TEXT NOT NULL,
  parentuid UUID,
  noteuid UUID NOT NULL,
  FOREIGN KEY (parentuid) REFERENCES public.articles (uid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (noteuid) REFERENCES public.notes (uid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE UNIQUE INDEX articles_id_uindex ON articles USING BTREE (id);

CREATE TABLE public.notes (
  uid UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
  mainarticleuid UUID,
  id INTEGER NOT NULL DEFAULT nextval('notes_id_seq'::regclass),
  FOREIGN KEY (mainarticleuid) REFERENCES public.articles (uid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE UNIQUE INDEX notes_id_uindex ON notes USING BTREE (id);


CREATE TABLE public.graph_data (
  article_uid UUID NOT NULL,
  note_uid UUID NOT NULL,
  x DOUBLE PRECISION NOT NULL,
  y DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (article_uid, note_uid),
  FOREIGN KEY (article_uid) REFERENCES public.articles (uid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (note_uid) REFERENCES public.notes (uid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE public.graph_data IS 'Данные по расположению статей на графе';

-- Postgres ------------------ ------

-- Table: articles

-- DROP TABLE articles;

CREATE TABLE articles
(
  uid uuid NOT NULL DEFAULT uuid_generate_v4(),
  id serial NOT NULL,
  title text NOT NULL,
  contenthtml text NOT NULL,
  contentmarkdown text NOT NULL,
  parentuid uuid,
  noteuid uuid NOT NULL,
  CONSTRAINT articles_pkey PRIMARY KEY (uid),
  CONSTRAINT articles_notes_uid_fk FOREIGN KEY (noteuid)
  REFERENCES notes (uid) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT articles_parentuid_fkey FOREIGN KEY (parentuid)
  REFERENCES articles (uid) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE articles
  OWNER TO postgres;

-- Index: articles_id_uindex

-- DROP INDEX articles_id_uindex;

CREATE UNIQUE INDEX articles_id_uindex
  ON articles
  USING btree
  (id);


-- Table: notes

-- DROP TABLE notes;

CREATE TABLE notes
(
  uid uuid NOT NULL DEFAULT uuid_generate_v4(),
  mainarticleuid uuid,
  id serial NOT NULL,
  CONSTRAINT notes_pkey PRIMARY KEY (uid),
  CONSTRAINT notes_mainarticleuid_fkey FOREIGN KEY (mainarticleuid)
  REFERENCES articles (uid) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE notes
  OWNER TO postgres;

-- Index: notes_id_uindex

-- DROP INDEX notes_id_uindex;

CREATE UNIQUE INDEX notes_id_uindex
  ON notes
  USING btree
  (id);
