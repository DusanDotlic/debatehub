--
-- PostgreSQL database dump
--

\restrict hTMOc1usuA3oeblaqgmTMSlcGTXMKiDgMNx1TTJtUtC4lb28z8DFNsdKprm1RzX

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

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
-- Name: public; Type: SCHEMA; Schema: -; Owner: debate_user
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO debate_user;

--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: trg_set_updated_at(); Type: FUNCTION; Schema: public; Owner: debate_user
--

CREATE FUNCTION public.trg_set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.updated_at := NOW();
  RETURN NEW;
END $$;


ALTER FUNCTION public.trg_set_updated_at() OWNER TO debate_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: auth_password_reset_tokens; Type: TABLE; Schema: public; Owner: debate_user
--

CREATE TABLE public.auth_password_reset_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    token text NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    used_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT reset_token_future CHECK ((expires_at > now()))
);


ALTER TABLE public.auth_password_reset_tokens OWNER TO debate_user;

--
-- Name: debate_participants; Type: TABLE; Schema: public; Owner: debate_user
--

CREATE TABLE public.debate_participants (
    debate_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role text NOT NULL,
    joined_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT participants_role_valid CHECK ((role = ANY (ARRAY['host'::text, 'debater'::text, 'moderator'::text])))
);


ALTER TABLE public.debate_participants OWNER TO debate_user;

--
-- Name: debates; Type: TABLE; Schema: public; Owner: debate_user
--

CREATE TABLE public.debates (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    title text NOT NULL,
    slug text NOT NULL,
    host_user_id uuid,
    description text,
    is_invite_only boolean DEFAULT true NOT NULL,
    status text DEFAULT 'scheduled'::text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    started_at timestamp with time zone,
    ended_at timestamp with time zone,
    CONSTRAINT debates_slug_not_blank CHECK ((length(TRIM(BOTH FROM slug)) > 0)),
    CONSTRAINT debates_status_valid CHECK ((status = ANY (ARRAY['scheduled'::text, 'live'::text, 'ended'::text]))),
    CONSTRAINT debates_time_order CHECK (((ended_at IS NULL) OR (started_at IS NULL) OR (ended_at >= started_at)))
);


ALTER TABLE public.debates OWNER TO debate_user;

--
-- Name: invitations; Type: TABLE; Schema: public; Owner: debate_user
--

CREATE TABLE public.invitations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    debate_id uuid NOT NULL,
    role_granted text NOT NULL,
    code text NOT NULL,
    expires_at timestamp with time zone,
    max_uses integer DEFAULT 1 NOT NULL,
    uses integer DEFAULT 0 NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    accepted_by_user_id uuid,
    accepted_at timestamp with time zone,
    CONSTRAINT invitations_accept_pair CHECK ((((accepted_by_user_id IS NULL) AND (accepted_at IS NULL)) OR ((accepted_by_user_id IS NOT NULL) AND (accepted_at IS NOT NULL)))),
    CONSTRAINT invitations_role_valid CHECK ((role_granted = ANY (ARRAY['debater'::text, 'moderator'::text]))),
    CONSTRAINT invitations_uses_bounds CHECK (((max_uses >= 1) AND (uses >= 0) AND (uses <= max_uses)))
);


ALTER TABLE public.invitations OWNER TO debate_user;

--
-- Name: user_pins; Type: TABLE; Schema: public; Owner: debate_user
--

CREATE TABLE public.user_pins (
    user_id uuid NOT NULL,
    debate_id uuid NOT NULL,
    pinned_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.user_pins OWNER TO debate_user;

--
-- Name: users; Type: TABLE; Schema: public; Owner: debate_user
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    email text NOT NULL,
    password_hash text NOT NULL,
    display_name text NOT NULL,
    avatar_url text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT users_email_not_blank CHECK ((length(TRIM(BOTH FROM email)) > 0)),
    CONSTRAINT users_pwd_not_blank CHECK ((length(password_hash) > 0))
);


ALTER TABLE public.users OWNER TO debate_user;

--
-- Data for Name: auth_password_reset_tokens; Type: TABLE DATA; Schema: public; Owner: debate_user
--

COPY public.auth_password_reset_tokens (id, user_id, token, expires_at, used_at, created_at) FROM stdin;
\.


--
-- Data for Name: debate_participants; Type: TABLE DATA; Schema: public; Owner: debate_user
--

COPY public.debate_participants (debate_id, user_id, role, joined_at) FROM stdin;
5f879865-00ca-4b01-883c-6712e5d2bb91	9e0205be-25e7-4965-b532-91da009f9a99	host	2025-09-04 18:40:28.527434+02
416ddb47-723f-4001-bf8c-2c3cbbddbb01	9e0205be-25e7-4965-b532-91da009f9a99	host	2025-09-04 18:59:07.408568+02
d6da1e6c-ac78-4e78-b397-52454f6eef08	9e0205be-25e7-4965-b532-91da009f9a99	host	2025-09-04 18:59:25.179471+02
1a0dd5e0-1de8-4954-8e22-1a11bff9f8ff	9e0205be-25e7-4965-b532-91da009f9a99	host	2025-09-04 19:13:13.151006+02
5f879865-00ca-4b01-883c-6712e5d2bb91	ddb10f50-f5cc-4ee9-8b68-e49933877c7e	debater	2025-09-05 13:03:42.890721+02
de6b9405-33c6-44e2-b762-708756d05e5f	cf8e2263-6a55-4f5c-82a3-fedbad59d884	host	2025-09-05 15:59:29.773118+02
323134fc-2dea-451d-964b-fe2e0ec1e1d0	cf8e2263-6a55-4f5c-82a3-fedbad59d884	host	2025-09-05 15:59:59.147966+02
5f879865-00ca-4b01-883c-6712e5d2bb91	cf8e2263-6a55-4f5c-82a3-fedbad59d884	debater	2025-09-05 16:00:33.767084+02
5f879865-00ca-4b01-883c-6712e5d2bb91	22b308ae-ed06-45da-a346-8c8273b3c75a	debater	2025-09-05 19:57:57.239876+02
52545c10-b4b1-46ae-81a0-a591db06a8ba	1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	host	2025-09-05 23:42:07.406208+02
5b4424d9-5e5f-444a-9165-10b18963a467	e18a9eb5-96cd-40ba-8e35-531590e82dd7	host	2025-09-05 23:51:44.360194+02
4c9c24e8-985b-43e5-88b1-271d56fd4a12	1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	host	2025-09-06 00:52:58.064585+02
bd30e257-8f01-4249-a77d-79a64ce6b64c	4f1a6abd-31e4-4f34-a575-8b36f77c5a7e	host	2025-09-06 09:16:16.327872+02
78d2c250-06a7-4ec2-9a0f-dca1e7eaa426	e18a9eb5-96cd-40ba-8e35-531590e82dd7	host	2025-09-06 15:37:48.852695+02
f64871c9-da38-4efd-b5c1-77cf919552a5	23c03376-0b7e-4a3a-b77b-4ec61d4be9d3	host	2025-09-06 23:26:26.413492+02
ebc1a5b0-c9f0-4278-885a-ba28414d213a	19581d80-260b-4413-8689-a0720d85949f	host	2025-09-06 23:28:33.835019+02
7c38f7e5-fbf9-4e6b-b6cf-f54c6e871ce5	68d8b463-57eb-4591-923a-7175baa931f5	host	2025-09-07 00:27:36.673222+02
602f94c4-2f2d-4cee-81c0-da92d9e73ddb	cbf136c7-1172-41a9-a4a0-775091de790b	host	2025-09-07 00:33:16.614232+02
038a698f-9e2f-4e99-92d6-f01799a31915	457a87c0-3971-4380-867d-bf3443f949c9	host	2025-09-07 00:40:45.223724+02
894b4133-0f28-4c2a-820d-519287c177a6	6102f94d-d9bc-450d-8817-cb12b8d80b49	host	2025-09-07 00:46:57.769358+02
ad1f37de-c0e9-41fe-8573-4d2c32a2942f	c2fb49d4-3514-4e5c-bee1-5a77fa3d1427	host	2025-09-07 09:26:33.481753+02
c991ab20-0d89-4ca0-a7a6-2d90f0f7f4a0	62ac7b34-434e-43e9-b82b-23c154005026	host	2025-09-07 09:29:44.13959+02
503ef96e-6272-4e23-a666-816a3957064f	a04de63a-acc9-4d47-bc2f-76dda569c20e	host	2025-09-07 09:30:13.256492+02
3a3e3207-afc6-4b66-a905-af7ffdb8facd	1a6251a6-5ac2-4c50-8bcc-ccf871825408	host	2025-09-07 09:31:16.889492+02
703d29eb-acf5-4739-a1bf-33a93552126c	e269e751-bf17-4f24-adb6-915cd279d202	host	2025-09-07 09:32:49.255217+02
0ccd3fc0-1c10-4c6f-9a60-95d9969c36d8	a7343952-5341-4d04-bf51-f9b5e8defab7	host	2025-09-07 10:14:20.711494+02
689f3578-9887-4983-be2d-5dfaf64940f5	d3af79f8-c520-4971-8778-8c29beb33d25	host	2025-09-07 10:16:12.662799+02
1000b223-8fff-4b6e-9e0b-c128f8016c08	c99a9f6d-6d8d-4d5b-a1a1-f98f4d6aad47	host	2025-09-07 10:50:14.885453+02
f42d9eef-ce15-4655-9d3d-d630b9cfd65c	c36f0402-5c70-420f-a59f-90ac55d80433	host	2025-09-07 10:59:39.857744+02
a3a9a5c1-8696-4089-955c-2440440593da	1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	host	2025-09-07 17:23:53.585827+02
fdbf533a-0887-407c-9f3b-c2732b830dec	b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	host	2025-09-07 19:42:44.486405+02
563fa3b5-3884-4a09-8df8-1b5b142dd41e	b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	host	2025-09-07 19:42:59.235987+02
3ed77001-e901-4378-975c-737a5c450eed	b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	host	2025-09-07 19:43:05.284067+02
\.


--
-- Data for Name: debates; Type: TABLE DATA; Schema: public; Owner: debate_user
--

COPY public.debates (id, title, slug, host_user_id, description, is_invite_only, status, created_at, updated_at, started_at, ended_at) FROM stdin;
5f879865-00ca-4b01-883c-6712e5d2bb91	Public AI Ethics Debate	public-ai-ethics-debate	9e0205be-25e7-4965-b532-91da009f9a99	Pros & cons	t	scheduled	2025-09-04 18:40:28.526715+02	2025-09-04 18:40:28.526715+02	\N	\N
416ddb47-723f-4001-bf8c-2c3cbbddbb01	Public AI Ethics Debate	public-ai-ethics-debate-nuir6z	9e0205be-25e7-4965-b532-91da009f9a99	Pros & cons	t	scheduled	2025-09-04 18:59:07.401128+02	2025-09-04 18:59:07.401128+02	\N	\N
d6da1e6c-ac78-4e78-b397-52454f6eef08	Public AI Ethics Debate	public-ai-ethics-debate-tjru8r	9e0205be-25e7-4965-b532-91da009f9a99	Pros & cons	t	scheduled	2025-09-04 18:59:25.17847+02	2025-09-04 18:59:25.17847+02	\N	\N
1a0dd5e0-1de8-4954-8e22-1a11bff9f8ff	Public AI Ethics Debate	public-ai-ethics-debate-50d31s	9e0205be-25e7-4965-b532-91da009f9a99	Pros & cons	t	scheduled	2025-09-04 19:13:13.141998+02	2025-09-04 19:13:13.141998+02	\N	\N
de6b9405-33c6-44e2-b762-708756d05e5f	Test Debate 1	test-debate-1	cf8e2263-6a55-4f5c-82a3-fedbad59d884	Description: Invite only!	t	scheduled	2025-09-05 15:59:29.76839+02	2025-09-05 15:59:29.76839+02	\N	\N
323134fc-2dea-451d-964b-fe2e0ec1e1d0	Test Debate 2	test-debate-2	cf8e2263-6a55-4f5c-82a3-fedbad59d884	Description: not invite only	f	scheduled	2025-09-05 15:59:59.146966+02	2025-09-05 15:59:59.146966+02	\N	\N
52545c10-b4b1-46ae-81a0-a591db06a8ba	1	1	1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	test	t	scheduled	2025-09-05 23:42:07.39095+02	2025-09-05 23:42:07.39095+02	\N	\N
5b4424d9-5e5f-444a-9165-10b18963a467	Sample Debate	sample-debate	e18a9eb5-96cd-40ba-8e35-531590e82dd7	Hello	t	scheduled	2025-09-05 23:51:44.359471+02	2025-09-05 23:51:44.359471+02	\N	\N
4c9c24e8-985b-43e5-88b1-271d56fd4a12	A	a	1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	asdasd	t	scheduled	2025-09-06 00:52:58.063584+02	2025-09-06 00:52:58.063584+02	\N	\N
bd30e257-8f01-4249-a77d-79a64ce6b64c	Ducina debata	ducina-debata	4f1a6abd-31e4-4f34-a575-8b36f77c5a7e	Testiram ovde svasta	t	scheduled	2025-09-06 09:16:16.327872+02	2025-09-06 09:16:16.327872+02	\N	\N
78d2c250-06a7-4ec2-9a0f-dca1e7eaa426	Test Debate	test-debate	e18a9eb5-96cd-40ba-8e35-531590e82dd7	Smoke test	t	scheduled	2025-09-06 15:37:48.851694+02	2025-09-06 15:37:48.851694+02	\N	\N
f64871c9-da38-4efd-b5c1-77cf919552a5	My First Debate 134016675855246013	my-first-debate-134016675855246013	23c03376-0b7e-4a3a-b77b-4ec61d4be9d3	Testing update/delete flow	f	scheduled	2025-09-06 23:26:26.412461+02	2025-09-06 23:26:26.412461+02	\N	\N
ebc1a5b0-c9f0-4278-885a-ba28414d213a	My First Debate 134016677135240418	my-first-debate-134016677135240418	19581d80-260b-4413-8689-a0720d85949f	Testing update/delete flow	f	scheduled	2025-09-06 23:28:33.834296+02	2025-09-06 23:28:33.834296+02	\N	\N
7c38f7e5-fbf9-4e6b-b6cf-f54c6e871ce5	My First Debate 134016712560030441	my-first-debate-134016712560030441	68d8b463-57eb-4591-923a-7175baa931f5	Testing update/delete flow	f	scheduled	2025-09-07 00:27:36.671716+02	2025-09-07 00:27:36.671716+02	\N	\N
602f94c4-2f2d-4cee-81c0-da92d9e73ddb	My First Debate 134016715961577704	my-first-debate-134016715961577704	cbf136c7-1172-41a9-a4a0-775091de790b	Testing update/delete flow	f	scheduled	2025-09-07 00:33:16.614232+02	2025-09-07 00:33:16.614232+02	\N	\N
038a698f-9e2f-4e99-92d6-f01799a31915	My First Debate 134016719616727645	my-first-debate-134016719616727645	457a87c0-3971-4380-867d-bf3443f949c9	Testing update/delete flow	f	scheduled	2025-09-07 00:40:45.223002+02	2025-09-07 00:40:45.223002+02	\N	\N
894b4133-0f28-4c2a-820d-519287c177a6	My First Debate 134016724137867716	my-first-debate-134016724137867716	6102f94d-d9bc-450d-8817-cb12b8d80b49	Testing update/delete flow	f	scheduled	2025-09-07 00:46:57.769358+02	2025-09-07 00:46:57.769358+02	\N	\N
ad1f37de-c0e9-41fe-8573-4d2c32a2942f	Pin Test 134017035827922748	pin-test-134017035827922748	c2fb49d4-3514-4e5c-bee1-5a77fa3d1427	pin test	f	scheduled	2025-09-07 09:26:33.480722+02	2025-09-07 09:26:33.480722+02	\N	\N
c991ab20-0d89-4ca0-a7a6-2d90f0f7f4a0	Pin Test 134017037839470505	pin-test-134017037839470505	62ac7b34-434e-43e9-b82b-23c154005026	pin test	f	scheduled	2025-09-07 09:29:44.13959+02	2025-09-07 09:29:44.13959+02	\N	\N
503ef96e-6272-4e23-a666-816a3957064f	Pin Test 134017038130697083	pin-test-134017038130697083	a04de63a-acc9-4d47-bc2f-76dda569c20e	pin test	f	scheduled	2025-09-07 09:30:13.256492+02	2025-09-07 09:30:13.256492+02	\N	\N
3a3e3207-afc6-4b66-a905-af7ffdb8facd	Pin Test 134017038767066654	pin-test-134017038767066654	1a6251a6-5ac2-4c50-8bcc-ccf871825408	pin test	f	scheduled	2025-09-07 09:31:16.889492+02	2025-09-07 09:31:16.889492+02	\N	\N
703d29eb-acf5-4739-a1bf-33a93552126c	Pin Test 134017039690605680	pin-test-134017039690605680	e269e751-bf17-4f24-adb6-915cd279d202	pin test	f	scheduled	2025-09-07 09:32:49.254934+02	2025-09-07 09:32:49.254934+02	\N	\N
0ccd3fc0-1c10-4c6f-9a60-95d9969c36d8	pin-test 134017064601312666	pin-test-134017064601312666	a7343952-5341-4d04-bf51-f9b5e8defab7	Pin demo	f	scheduled	2025-09-07 10:14:20.709493+02	2025-09-07 10:14:20.709493+02	\N	\N
689f3578-9887-4983-be2d-5dfaf64940f5	pin-test 134017065621790077	pin-test-134017065621790077	d3af79f8-c520-4971-8778-8c29beb33d25	Pin demo	f	scheduled	2025-09-07 10:16:12.66211+02	2025-09-07 10:16:12.66211+02	\N	\N
1000b223-8fff-4b6e-9e0b-c128f8016c08	mine-hosted-check 134017085969987044	mine-hosted-check-134017085969987044	c99a9f6d-6d8d-4d5b-a1a1-f98f4d6aad47	testing hosted/joined	f	scheduled	2025-09-07 10:50:14.884451+02	2025-09-07 10:50:14.884451+02	\N	\N
f42d9eef-ce15-4655-9d3d-d630b9cfd65c	mine-hosted-check 134017091648655261	mine-hosted-check-134017091648655261	c36f0402-5c70-420f-a59f-90ac55d80433	testing hosted/joined	f	scheduled	2025-09-07 10:59:39.857744+02	2025-09-07 10:59:39.857744+02	\N	\N
a3a9a5c1-8696-4089-955c-2440440593da	Joinable Debate	joinable-debate	1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	This is open to all	f	scheduled	2025-09-07 17:23:53.579821+02	2025-09-07 17:23:53.579821+02	\N	\N
3df810e1-118d-4e26-ae7e-1dd65ac9996e	Test4s Debate	test4s-debate	\N	Testing!	t	scheduled	2025-09-07 19:30:09.196205+02	2025-09-07 19:36:11.286993+02	\N	\N
fdbf533a-0887-407c-9f3b-c2732b830dec	Test	test	b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	1	t	scheduled	2025-09-07 19:42:44.486405+02	2025-09-07 19:42:44.486405+02	\N	\N
563fa3b5-3884-4a09-8df8-1b5b142dd41e	Test	test-vyjqam	b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	1	t	scheduled	2025-09-07 19:42:59.235987+02	2025-09-07 19:42:59.235987+02	\N	\N
3ed77001-e901-4378-975c-737a5c450eed	stats	stats	b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	asdasda	t	scheduled	2025-09-07 19:43:05.283066+02	2025-09-07 19:43:05.283066+02	\N	\N
\.


--
-- Data for Name: invitations; Type: TABLE DATA; Schema: public; Owner: debate_user
--

COPY public.invitations (id, debate_id, role_granted, code, expires_at, max_uses, uses, created_by, created_at, accepted_by_user_id, accepted_at) FROM stdin;
352c17be-279f-495c-99d3-6c238e25dd5f	5f879865-00ca-4b01-883c-6712e5d2bb91	debater	p4i55ttw	2025-09-12 13:03:10.814969+02	3	2	9e0205be-25e7-4965-b532-91da009f9a99	2025-09-05 13:03:10.814969+02	cf8e2263-6a55-4f5c-82a3-fedbad59d884	2025-09-05 16:00:33.767864+02
bab4fa0e-b7e9-4341-b085-eb6b9918041d	5f879865-00ca-4b01-883c-6712e5d2bb91	debater	9d4le8lx	2025-09-12 19:52:45.945543+02	3	1	9e0205be-25e7-4965-b532-91da009f9a99	2025-09-05 19:52:45.945543+02	22b308ae-ed06-45da-a346-8c8273b3c75a	2025-09-05 19:57:57.249926+02
37456fb9-d533-4d7b-8307-5e1d97e127a1	5f879865-00ca-4b01-883c-6712e5d2bb91	debater	fts3y4q8	2025-09-12 19:58:28.379867+02	3	0	9e0205be-25e7-4965-b532-91da009f9a99	2025-09-05 19:58:28.379867+02	\N	\N
\.


--
-- Data for Name: user_pins; Type: TABLE DATA; Schema: public; Owner: debate_user
--

COPY public.user_pins (user_id, debate_id, pinned_at) FROM stdin;
9e0205be-25e7-4965-b532-91da009f9a99	5f879865-00ca-4b01-883c-6712e5d2bb91	2025-09-05 13:21:22.371791+02
4f1a6abd-31e4-4f34-a575-8b36f77c5a7e	bd30e257-8f01-4249-a77d-79a64ce6b64c	2025-09-06 09:16:18.385132+02
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: debate_user
--

COPY public.users (id, email, password_hash, display_name, avatar_url, is_active, created_at, updated_at) FROM stdin;
c36f0402-5c70-420f-a59f-90ac55d80433	host+134017091648655261@example.com	$2a$10$1HHr97skhVZZLXdj7NNw3epBVEqR8IAMFz5rr0QacyOp9dHn5USYe	Host 134017091648655261	\N	t	2025-09-07 10:59:29.294393+02	2025-09-07 10:59:29.294393+02
9e0205be-25e7-4965-b532-91da009f9a99	host@example.com	$2a$10$auQ2MMnCZnkzRvPBlIszSOP6f1Lzw/K/mTjLuMm7tH9hj6HDJim6S	Hosty	\N	t	2025-09-04 18:40:22.066506+02	2025-09-04 18:40:22.066506+02
ddb10f50-f5cc-4ee9-8b68-e49933877c7e	guest@example.com	$2a$10$EWXv3.USzYRoCtZfJIodQ.fkNDy3yhl7pUGvAzfnFTU6qZBFNOewO	Guesty	\N	t	2025-09-05 12:59:41.605598+02	2025-09-05 12:59:41.605598+02
cf8e2263-6a55-4f5c-82a3-fedbad59d884	test@test.com	$2a$10$BOKadC4aHDg2xFihFBKEdOtOOk3OXLqwx.jvgTWwr7QiytTua0O8O	Tester	\N	t	2025-09-05 15:58:12.411711+02	2025-09-05 15:58:12.411711+02
1637a9f6-2e1a-41c1-9f0e-3cf69df392e6	test2@test.com	$2a$10$uPQPO8td9p1Xzkf2JxZOfe8RCYXwpIli08l8pj352vjluybONHL8O	Tester2	\N	t	2025-09-05 16:01:03.899991+02	2025-09-05 16:01:03.899991+02
ba54fa5b-1f95-4932-9c86-250bc41f4f24	user@example.com	$2a$10$woGTdwo6n.v/9aOdbLNDHOtX06S5k/dbKzjSgew4XTu.it6xEKXmG	user2	\N	t	2025-09-05 16:07:06.154556+02	2025-09-05 16:07:06.154556+02
22b308ae-ed06-45da-a346-8c8273b3c75a	user3@example.com	$2a$10$bDdqXOSVIWekXXbCcUJRheA/Cm6Ygbg93Fy.NtFjNmdreiIDBOLm.	User3	\N	t	2025-09-05 18:52:38.982427+02	2025-09-05 18:52:38.984429+02
e18a9eb5-96cd-40ba-8e35-531590e82dd7	demo@example.com	$2a$10$jkEjQmDE3i/H/5z5R.OEOeuc3Fw2OF72WziEOW86NGMDySWWQWZNq	Demo	\N	t	2025-09-05 23:51:00.916388+02	2025-09-05 23:51:00.916388+02
4f1a6abd-31e4-4f34-a575-8b36f77c5a7e	duca@test.com	$2a$10$HAFeODVXOfFHKp0ruOZ73.S6SFAiQmsvAoCLG3Gex7PERXZ5dcwKy	Duca	\N	t	2025-09-06 09:14:44.241857+02	2025-09-06 09:14:44.241857+02
1e8f270c-75b2-48ee-a41b-ee617c89b1fe	demo2@example.com	$2a$10$quR13ysfYZcxct89JeQcv.vx5vsU23j.YvSFymfHnMTcx9evhQdnS	Demo Two	\N	t	2025-09-06 15:18:11.840531+02	2025-09-06 15:18:11.840531+02
9a3cdbfb-46ca-4e7a-8233-bae92a2445b8	host+134017162827801272@example.com	$2a$10$/2uVIxDEuuSoGjfSkjJoV.hOmUp65YlhNpAZU8V58cFxenCO8lfw6	Host 134017162827801272	\N	t	2025-09-07 12:58:07.589712+02	2025-09-07 12:58:07.589712+02
23c03376-0b7e-4a3a-b77b-4ec61d4be9d3	host+134016675855246013@example.com	$2a$10$RsJFvrk1.WKO24.DAPnwEenl3TN7DEguuUBV/qp86WBaxT5SumLZi	Host 134016675855246013	\N	t	2025-09-06 23:26:25.723172+02	2025-09-06 23:26:25.724173+02
8b1d8ab5-29bc-4e74-a4d8-c6c659cfd334	attacker+134016675855246013@example.com	$2a$10$wSeE0LLGNBabg/W/0xxQB.yHAqaaVptprSAf0uEW6Gu30hKF9hjQG	Attacker 134016675855246013	\N	t	2025-09-06 23:26:27.127168+02	2025-09-06 23:26:27.127168+02
19581d80-260b-4413-8689-a0720d85949f	host+134016677135240418@example.com	$2a$10$R.kqKcRJfdZgdUKahOiizukCrfUP/igHOQkyTlUhBXz817pPk8Pme	Host 134016677135240418	\N	t	2025-09-06 23:28:33.556578+02	2025-09-06 23:28:33.556578+02
c1f97b03-a6c0-4210-a3ed-82e02a2e20e7	attacker+134016677135240418@example.com	$2a$10$UFVHmMMLzpRzmtOuBCQksum2jrZd0X0xXIh3b4XxYNF/RjC0eEmxi	Attacker 134016677135240418	\N	t	2025-09-06 23:28:34.086722+02	2025-09-06 23:28:34.086722+02
68d8b463-57eb-4591-923a-7175baa931f5	host+134016712560030441@example.com	$2a$10$HP8BNL3olSbaaGsYGoudbe2jMQwmD180Pz22IzSmLh10cVccWv/6.	Host 134016712560030441	\N	t	2025-09-07 00:27:36.135111+02	2025-09-07 00:27:36.136112+02
8cc49b40-611f-4887-84b0-37212f12422c	attacker+134016712560030441@example.com	$2a$10$bmIde4jAv9JVBlxBljeeNuhhgzAkbtenYCna1xaLIFzGtuAP82o4q	Attacker 134016712560030441	\N	t	2025-09-07 00:27:37.139619+02	2025-09-07 00:27:37.139619+02
cbf136c7-1172-41a9-a4a0-775091de790b	host+134016715961577704@example.com	$2a$10$vCp/5oCuSzFI0QmrTd74Suto1aI/7VB0WI2sEvOfNsDMbsUSE9deO	Host 134016715961577704	\N	t	2025-09-07 00:33:16.190843+02	2025-09-07 00:33:16.190843+02
f82078ed-6fb5-46ca-8096-d333a8ab69b1	attacker+134016715961577704@example.com	$2a$10$nuD.1s.7Bvtp1ELSa9M5Xu74ZUHA68cI/OTb57SSl4TiO0htmkGGi	Attacker 134016715961577704	\N	t	2025-09-07 00:33:17.071916+02	2025-09-07 00:33:17.071916+02
457a87c0-3971-4380-867d-bf3443f949c9	host+134016719616727645@example.com	$2a$10$ula5FhUSFLSRDa.hhynIwO9be89T0HPgSu.MBG5/XurFfMVvT74yO	Host 134016719616727645	\N	t	2025-09-07 00:39:21.700256+02	2025-09-07 00:39:21.700256+02
be5d2a5e-8a3f-490a-ba6a-a0d415cc61e0	attacker+134016719616727645@example.com	$2a$10$nme2VmQjMB0CGfhpOCBPkOZRKj1tt0cFfhwa7fN.sBMjXFaP2ijYO	Attacker 134016719616727645	\N	t	2025-09-07 00:41:12.060585+02	2025-09-07 00:41:12.060585+02
6102f94d-d9bc-450d-8817-cb12b8d80b49	host+134016724137867716@example.com	$2a$10$nXGGnbSBHGbE3V6Oi0AMQOnhsyN72IgpCxzqTKP6aoAuDJlB7gery	Host 134016724137867716	\N	t	2025-09-07 00:46:53.817266+02	2025-09-07 00:46:53.817266+02
0a99822c-1004-40f9-8c2f-9135a8e83bdd	attacker+134016724137867716@example.com	$2a$10$Ibkq9Puipg3aHWzfM89JzeVQR5pDMBVaZjDuL5AMh36.SHcfeHYQ2	Attacker 134016724137867716	\N	t	2025-09-07 00:47:07.794174+02	2025-09-07 00:47:07.794174+02
b79a1ad3-a4e2-458b-b9bf-1fff439958b4	host+134016738985142920@example.com	$2a$10$dQDE1MMW9qyLhHWRg4apq.HD3u92FycQ9xwsXHNXe9B4K7/rJM0oG	Host 134016738985142920	\N	t	2025-09-07 01:11:38.884536+02	2025-09-07 01:11:38.884536+02
865284e6-2972-4210-9cc8-0a85993a7e69	host+134016740792789333@example.com	$2a$10$JzCa2EY2GZAJtmi0yubxCeR41ui.ELQgBtwZjjJ3EsK3DuhbJ4Rfy	Host 134016740792789333	\N	t	2025-09-07 01:14:39.298982+02	2025-09-07 01:14:39.298982+02
765678d4-106b-4dbf-b7e3-afedfc7f2ceb	attacker+134016740792789333@example.com	$2a$10$AOdl3dT1d1oxtDHuaa3t9Of6U4j6x1CVag0E6Y3MUldo82UuNi9sK	Attacker 134016740792789333	\N	t	2025-09-07 01:14:39.702448+02	2025-09-07 01:14:39.702448+02
1df47041-4351-4d37-b4cf-8cc3bf245bb9	smoke+134017028823472092@example.com	$2a$10$JAv6BW.C5mj61YZq9FwD5.t6UzD0yxqLzQbiQFW89e2Bx5m34t912	Smoke 134017028823472092	\N	t	2025-09-07 09:14:54.61962+02	2025-09-07 09:15:02.350881+02
d538e7b2-a151-47f8-a695-310a606f66d0	attacker+134017028823472092@example.com	$2a$10$NBVX4LUANxPWFdOPEHUCVeR2uyxw057VWGQShP.je9ZOsCwDa53gu	Attacker 134017028823472092	\N	t	2025-09-07 09:15:48.467038+02	2025-09-07 09:15:48.467038+02
c2fb49d4-3514-4e5c-bee1-5a77fa3d1427	pinhost+134017035827922748@example.com	$2a$10$br/38vGx90j/l93cuh4/QeFKgYt7UDK9AI2tOhVSDxWRnPgU/dey.	Pin Host 134017035827922748	\N	t	2025-09-07 09:26:28.045446+02	2025-09-07 09:26:28.045446+02
62ac7b34-434e-43e9-b82b-23c154005026	pinhost+134017037839470505@example.com	$2a$10$4eahqzzEFQQJHLpDsMhxU.Ld/Y2tMmW5L577Wa2gyfMaJKWG/owg6	Pin Host 134017037839470505	\N	t	2025-09-07 09:29:43.970422+02	2025-09-07 09:29:43.970422+02
a04de63a-acc9-4d47-bc2f-76dda569c20e	pinhost+134017038130697083@example.com	$2a$10$qWX6WNANNhid0JhRsd3JQO5quHhcQNMktsITZHzvFk8OSIocOKUYe	Pin Host 134017038130697083	\N	t	2025-09-07 09:30:13.089407+02	2025-09-07 09:30:13.089407+02
1a6251a6-5ac2-4c50-8bcc-ccf871825408	pinhost+134017038767066654@example.com	$2a$10$4KO2GMz7RBUb/JYNWtnQLuf.MadVLUpetGwLy14KzAq7QJ3ipj4o2	Pin Host 134017038767066654	\N	t	2025-09-07 09:31:16.727103+02	2025-09-07 09:31:16.727103+02
e269e751-bf17-4f24-adb6-915cd279d202	pinhost+134017039690605680@example.com	$2a$10$m5xMkx5sRgtV5cOIwOMj4eLnhJUAwQ2yPrxyhQdDSF9SrJVSS0Uei	Pin Host 134017039690605680	\N	t	2025-09-07 09:32:49.083589+02	2025-09-07 09:32:49.083589+02
a7343952-5341-4d04-bf51-f9b5e8defab7	host+134017064601312666@example.com	$2a$10$H/QfEmIJWrrrouG0aQTj6e98abaLRSOLNTf4pONiN.F8hRN7HIJwm	Host 134017064601312666	\N	t	2025-09-07 10:14:20.398238+02	2025-09-07 10:14:20.398238+02
d3af79f8-c520-4971-8778-8c29beb33d25	host+134017065621790077@example.com	$2a$10$CyW9hDZ03YlFRXJUg6Lshuw93AXjHEIuHpM.y7s9PIlKu.lZbWTqi	Host 134017065621790077	\N	t	2025-09-07 10:16:06.806933+02	2025-09-07 10:16:06.806933+02
b53b423e-6d4f-4f8e-a431-1fd0695b9a69	hostA+134017074584306660@example.com	$2a$10$eGzGDBxZVQH/nr.sejuYsO1t3cUHVD7QPb82R3RuGl70.hYCkTuca	Host A 134017074584306660	\N	t	2025-09-07 10:30:58.578553+02	2025-09-07 10:30:58.578553+02
649470f7-e8b3-4342-9427-305715ff65ef	joiner+134017074584306660@example.com	$2a$10$ly33KUoriYVzwhDAC/16ieqr8SGbOHtoVNzUR87XA0JUwZpAuZcB6	Joiner 134017074584306660	\N	t	2025-09-07 10:30:59.310617+02	2025-09-07 10:30:59.310617+02
c99a9f6d-6d8d-4d5b-a1a1-f98f4d6aad47	host+134017085969987044@example.com	$2a$10$5u3YW0x/FLO95cDaCPw/T.VHBmgnhNCCdmp1T/05SdzPCMYNaKOtm	Host 134017085969987044	\N	t	2025-09-07 10:49:57.127692+02	2025-09-07 10:49:57.128693+02
4d6028a0-43aa-4ed1-af2a-53b275f1fc63	att+134017162827801272@example.com	$2a$10$34IDNwa7vNq.PAoVR1pG5.xqDTHdbos2e0O4tTa1CkpaacgFd99lO	Att 134017162827801272	\N	t	2025-09-07 12:59:47.385417+02	2025-09-07 12:59:47.385417+02
b7d8a0c2-e06f-4d8e-b05e-2ebbdad9abac	test4@test.com	$2a$10$rAGxgdzXodfmrXiD1xm1r.Xh1BeASNNtBDk1XhluxoyebYS4WVvMW	Test4	\N	t	2025-09-07 19:42:21.972057+02	2025-09-07 19:42:21.972057+02
\.


--
-- Name: auth_password_reset_tokens auth_password_reset_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.auth_password_reset_tokens
    ADD CONSTRAINT auth_password_reset_tokens_pkey PRIMARY KEY (id);


--
-- Name: debate_participants debate_participants_pkey; Type: CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.debate_participants
    ADD CONSTRAINT debate_participants_pkey PRIMARY KEY (debate_id, user_id);


--
-- Name: debates debates_pkey; Type: CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.debates
    ADD CONSTRAINT debates_pkey PRIMARY KEY (id);


--
-- Name: invitations invitations_pkey; Type: CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_pkey PRIMARY KEY (id);


--
-- Name: user_pins user_pins_pkey; Type: CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.user_pins
    ADD CONSTRAINT user_pins_pkey PRIMARY KEY (user_id, debate_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: ix_debates_host; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_debates_host ON public.debates USING btree (host_user_id);


--
-- Name: ix_debates_status; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_debates_status ON public.debates USING btree (status);


--
-- Name: ix_invitations_debate; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_invitations_debate ON public.invitations USING btree (debate_id);


--
-- Name: ix_participants_debate; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_participants_debate ON public.debate_participants USING btree (debate_id);


--
-- Name: ix_participants_user; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_participants_user ON public.debate_participants USING btree (user_id);


--
-- Name: ix_reset_tokens_user; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_reset_tokens_user ON public.auth_password_reset_tokens USING btree (user_id);


--
-- Name: ix_user_pins_debate; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_user_pins_debate ON public.user_pins USING btree (debate_id);


--
-- Name: ix_user_pins_user; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE INDEX ix_user_pins_user ON public.user_pins USING btree (user_id);


--
-- Name: ux_debates_slug; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE UNIQUE INDEX ux_debates_slug ON public.debates USING btree (slug);


--
-- Name: ux_invitations_code; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE UNIQUE INDEX ux_invitations_code ON public.invitations USING btree (code);


--
-- Name: ux_reset_tokens_token; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE UNIQUE INDEX ux_reset_tokens_token ON public.auth_password_reset_tokens USING btree (token);


--
-- Name: ux_users_email_ci; Type: INDEX; Schema: public; Owner: debate_user
--

CREATE UNIQUE INDEX ux_users_email_ci ON public.users USING btree (lower(email));


--
-- Name: debates debates_set_updated_at; Type: TRIGGER; Schema: public; Owner: debate_user
--

CREATE TRIGGER debates_set_updated_at BEFORE UPDATE ON public.debates FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();


--
-- Name: users users_set_updated_at; Type: TRIGGER; Schema: public; Owner: debate_user
--

CREATE TRIGGER users_set_updated_at BEFORE UPDATE ON public.users FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();


--
-- Name: auth_password_reset_tokens auth_password_reset_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.auth_password_reset_tokens
    ADD CONSTRAINT auth_password_reset_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: debate_participants debate_participants_debate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.debate_participants
    ADD CONSTRAINT debate_participants_debate_id_fkey FOREIGN KEY (debate_id) REFERENCES public.debates(id) ON DELETE CASCADE;


--
-- Name: debate_participants debate_participants_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.debate_participants
    ADD CONSTRAINT debate_participants_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: debates debates_host_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.debates
    ADD CONSTRAINT debates_host_user_id_fkey FOREIGN KEY (host_user_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: invitations invitations_accepted_by_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_accepted_by_user_id_fkey FOREIGN KEY (accepted_by_user_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: invitations invitations_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- Name: invitations invitations_debate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.invitations
    ADD CONSTRAINT invitations_debate_id_fkey FOREIGN KEY (debate_id) REFERENCES public.debates(id) ON DELETE CASCADE;


--
-- Name: user_pins user_pins_debate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.user_pins
    ADD CONSTRAINT user_pins_debate_id_fkey FOREIGN KEY (debate_id) REFERENCES public.debates(id) ON DELETE CASCADE;


--
-- Name: user_pins user_pins_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: debate_user
--

ALTER TABLE ONLY public.user_pins
    ADD CONSTRAINT user_pins_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict hTMOc1usuA3oeblaqgmTMSlcGTXMKiDgMNx1TTJtUtC4lb28z8DFNsdKprm1RzX

