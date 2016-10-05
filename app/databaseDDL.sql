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