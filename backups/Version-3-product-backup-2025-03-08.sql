PGDMP  .    "                }            stock    17.2    17.2     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            �           1262    16403    stock    DATABASE     �   CREATE DATABASE stock WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE stock;
                     postgres    false            �            1259    16582    products    TABLE     �   CREATE TABLE public.products (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    stock_qty integer NOT NULL,
    import_date date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.products;
       public         heap r       postgres    false            �            1259    16586    products_id_seq    SEQUENCE     �   CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.products_id_seq;
       public               postgres    false    217                        0    0    products_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;
          public               postgres    false    218            �            1259    16587    settings    TABLE     �   CREATE TABLE public.settings (
    id integer NOT NULL,
    key_name character varying(50) NOT NULL,
    value character varying(50) NOT NULL
);
    DROP TABLE public.settings;
       public         heap r       postgres    false            �            1259    16590    settings_id_seq    SEQUENCE     �   CREATE SEQUENCE public.settings_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.settings_id_seq;
       public               postgres    false    219                       0    0    settings_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.settings_id_seq OWNED BY public.settings.id;
          public               postgres    false    220            \           2604    16591    products id    DEFAULT     j   ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);
 :   ALTER TABLE public.products ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    218    217            ^           2604    16592    settings id    DEFAULT     j   ALTER TABLE ONLY public.settings ALTER COLUMN id SET DEFAULT nextval('public.settings_id_seq'::regclass);
 :   ALTER TABLE public.settings ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    220    219            �          0    16582    products 
   TABLE DATA           P   COPY public.products (id, name, unit_price, stock_qty, import_date) FROM stdin;
    public               postgres    false    217   �       �          0    16587    settings 
   TABLE DATA           7   COPY public.settings (id, key_name, value) FROM stdin;
    public               postgres    false    219   �                  0    0    products_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.products_id_seq', 12, true);
          public               postgres    false    218                       0    0    settings_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.settings_id_seq', 8, true);
          public               postgres    false    220            `           2606    16594    products products_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.products DROP CONSTRAINT products_pkey;
       public                 postgres    false    217            b           2606    16596    settings settings_key_name_key 
   CONSTRAINT     ]   ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_key_name_key UNIQUE (key_name);
 H   ALTER TABLE ONLY public.settings DROP CONSTRAINT settings_key_name_key;
       public                 postgres    false    219            d           2606    16598    settings settings_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.settings DROP CONSTRAINT settings_pkey;
       public                 postgres    false    219            �   �   x�e�Aj�0E�3���Ȳ�Z˴āB�ҍ���mI���u�B������pj=w��N�-@
�6"���ھ�t���Y��ܩlEe�@f��F�!S����{��ظ�郎�r�pR?8�9<wG�bC{��%�iA�J����g�Ӿ�Tb�Ohp���|�u���XeOR��o�]��T`�Be��Fچ)̝"���H,��p���#U�r��|5��� Ts      �      x�3�,�/�����,�4����� :�      