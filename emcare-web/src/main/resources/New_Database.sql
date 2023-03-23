ALTER DATABASE emcare OWNER TO postgres;

CREATE SCHEMA IF NOT EXISTS public;

ALTER SCHEMA public OWNER TO postgres;

CREATE FUNCTION public.array_reverse(anyarray) RETURNS anyarray
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
SELECT ARRAY(
    SELECT $1[i]
    FROM generate_series(
        array_lower($1,1),
        array_upper($1,1)
    ) AS s(i)
    ORDER BY i DESC
);
$_$;


ALTER FUNCTION public.array_reverse(anyarray) OWNER TO postgres;

CREATE TABLE public.activity_definition_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);

ALTER TABLE public.activity_definition_resource OWNER TO postgres;

CREATE TABLE public.code_system_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);


ALTER TABLE public.code_system_resource OWNER TO postgres;

CREATE TABLE public.condition_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    encounter_id character varying(255),
    patient_id character varying(255),
    resource_id character varying(255),
    text text
);


ALTER TABLE public.condition_resource OWNER TO postgres;

CREATE TABLE public.device_master (
    device_id integer NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    android_version character varying(255) NOT NULL,
    device_model character varying(255) NOT NULL,
    device_name character varying(255) NOT NULL,
    device_os character varying(255) NOT NULL,
    device_uuid character varying(255) NOT NULL,
    imei_number character varying(255),
    is_blocked boolean NOT NULL,
    last_logged_in_user character varying(255) NOT NULL,
    mac_address character varying(255),
    user_name character varying(255),
    ig_version character varying(255)
);


ALTER TABLE public.device_master OWNER TO postgres;

CREATE TABLE public.email_content (
    id bigint NOT NULL,
    code character varying(255) NOT NULL,
    content text NOT NULL,
    created_at timestamp without time zone NOT NULL,
    subject character varying(255) NOT NULL,
    var_list text
);


ALTER TABLE public.email_content OWNER TO postgres;

CREATE TABLE public.emcare_custom_code_system (
    code_id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    code character varying(255) NOT NULL,
    code_description character varying(255)
);


ALTER TABLE public.emcare_custom_code_system OWNER TO postgres;

CREATE TABLE public.emcare_resources (
    id integer NOT NULL,
    resource_id character varying(255),
    text text,
    type text,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    updated_by uuid,
    facility_id character varying(255)
);


ALTER TABLE public.emcare_resources OWNER TO postgres;

CREATE TABLE public.encounter_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    patient_id character varying(255),
    resource_id character varying(255),
    text text
);


ALTER TABLE public.encounter_resource OWNER TO postgres;

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

CREATE TABLE public.hierarchy_master (
    hierarchy_type character varying(255) NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    code character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.hierarchy_master OWNER TO postgres;

CREATE TABLE public.indicator (
    indicator_id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    denominator_indicator_equation character varying(255),
    description text,
    facility_id character varying(255),
    indicator_code character varying(255),
    indicator_name character varying(255),
    numerator_indicator_equation character varying(255),
    display_type character varying(255),
    denominator_equation_string text,
    numerator_equation_string text
);


ALTER TABLE public.indicator OWNER TO postgres;

CREATE TABLE public.indicator_denominator_equation (
    denominator_id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    code character varying(255) NOT NULL,
    code_id bigint,
    condition character varying(255),
    value character varying(255),
    value_type character varying(255),
    indicator_id bigint NOT NULL,
    eq_identifier character varying(255)
);


ALTER TABLE public.indicator_denominator_equation OWNER TO postgres;

CREATE TABLE public.indicator_numerator_equation (
    numerator_id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    code character varying(255) NOT NULL,
    code_id bigint,
    condition character varying(255),
    value character varying(255),
    value_type character varying(255),
    indicator_id bigint NOT NULL,
    eq_identifier character varying(255)
);


ALTER TABLE public.indicator_numerator_equation OWNER TO postgres;

CREATE TABLE public.language_translation (
    id integer NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    language_code character varying(255) NOT NULL,
    language_data text,
    language_name character varying(255) NOT NULL
);


ALTER TABLE public.language_translation OWNER TO postgres;

CREATE TABLE public.library_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);


ALTER TABLE public.library_resource OWNER TO postgres;

CREATE TABLE public.location_master (
    id integer NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    is_active boolean NOT NULL,
    name character varying(4000) NOT NULL,
    parent bigint,
    type character varying(10) NOT NULL
);


ALTER TABLE public.location_master OWNER TO postgres;

CREATE TABLE public.location_resources (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text,
    type text,
    location_id bigint,
    org_id character varying(255),
    location_name character varying(255),
    organization_name character varying(255)
);


ALTER TABLE public.location_resources OWNER TO postgres;

CREATE TABLE public.medication_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);


ALTER TABLE public.medication_resource OWNER TO postgres;

CREATE TABLE public.menu_config (
    id integer NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    feature_json character varying(255) NOT NULL,
    is_active character varying(255) NOT NULL,
    menu_name character varying(255) NOT NULL,
    parent character varying(255),
    order_number bigint
);


ALTER TABLE public.menu_config OWNER TO postgres;

CREATE TABLE public.observation_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    subject_id character varying(255),
    subject_type character varying(255),
    text text,
    ident text GENERATED ALWAYS AS (((text)::json -> 'id'::text)) STORED
);


ALTER TABLE public.observation_resource OWNER TO postgres;

CREATE TABLE public.operation_definition_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);


ALTER TABLE public.operation_definition_resource OWNER TO postgres;

CREATE TABLE public.organization_resources (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text,
    type text
);


ALTER TABLE public.organization_resources OWNER TO postgres;

CREATE TABLE public.otp (
    id bigint NOT NULL,
    count integer NOT NULL,
    email_id character varying(255) NOT NULL,
    expiry timestamp without time zone NOT NULL,
    otp character varying(255) NOT NULL,
    verified boolean NOT NULL
);


ALTER TABLE public.otp OWNER TO postgres;

CREATE TABLE public.plan_definition_resources (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text,
    type text
);


ALTER TABLE public.plan_definition_resources OWNER TO postgres;

CREATE TABLE public.questionnaire_master (
    id integer NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    text text,
    version text,
    resource_id text
);


ALTER TABLE public.questionnaire_master OWNER TO postgres;

CREATE TABLE public.questionnaire_response (
    id character varying(255) NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    consultation_stage character varying(255),
    encounter_id character varying(255),
    is_active boolean,
    patient_id character varying(255),
    questionnaire_id character varying(255),
    questionnaire_response text,
    structure_map_id character varying(255),
    consultation_date timestamp without time zone
);


ALTER TABLE public.questionnaire_response OWNER TO postgres;

CREATE TABLE public.related_person_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    patient_id character varying(255),
    resource_id character varying(255),
    text text
);


ALTER TABLE public.related_person_resource OWNER TO postgres;

CREATE TABLE public.settings (
    id bigint NOT NULL,
    key character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(255) NOT NULL
);


ALTER TABLE public.settings OWNER TO postgres;

CREATE TABLE public.structure_definition_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);


ALTER TABLE public.structure_definition_resource OWNER TO postgres;

CREATE TABLE public.structure_map_resource (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text
);


ALTER TABLE public.structure_map_resource OWNER TO postgres;

CREATE TABLE public.tutorials (
    id integer NOT NULL,
    description character varying(255),
    published boolean,
    title character varying(255)
);


ALTER TABLE public.tutorials OWNER TO postgres;

CREATE TABLE public.user_location_mapping (
    id integer NOT NULL,
    location_id integer,
    state boolean NOT NULL,
    user_id character varying(255) NOT NULL,
    reg_request_from character varying(255) NOT NULL,
    is_first boolean DEFAULT true NOT NULL,
    create_date timestamp without time zone,
    facility_id character varying(255)
);


ALTER TABLE public.user_location_mapping OWNER TO postgres;

CREATE TABLE public.user_master (
    user_id character varying(255) NOT NULL,
    location_id integer,
    reg_request_from character varying(255) NOT NULL,
    reg_status character varying(255) NOT NULL
);


ALTER TABLE public.user_master OWNER TO postgres;

CREATE TABLE public.user_menu_config (
    id integer NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    feature_json character varying(255),
    menu_id integer NOT NULL,
    role_id character varying(255),
    user_id character varying(255)
);


ALTER TABLE public.user_menu_config OWNER TO postgres;

CREATE TABLE public.value_set_resources (
    id bigint NOT NULL,
    created_by character varying(255) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    modified_by character varying(255),
    modified_on timestamp without time zone,
    resource_id character varying(255),
    text text,
    type text
);


ALTER TABLE public.value_set_resources OWNER TO postgres;

INSERT INTO public.email_content (id, code, content, created_at, subject, var_list) VALUES (3, 'CONFIRMATION_EMAIL_APPROVED', '<!DOCTYPE html>
<html>
<head>

  <meta charset="utf-8">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style type="text/css">
  @media screen {
    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 400;
      src: local(''Source Sans Pro Regular''), local(''SourceSansPro-Regular''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format(''woff'');
    }

    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 700;
      src: local(''Source Sans Pro Bold''), local(''SourceSansPro-Bold''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format(''woff'');
    }
  }
  body,
  table,
  td,
  a {
    -ms-text-size-adjust: 100%; /* 1 */
    -webkit-text-size-adjust: 100%; /* 2 */
  }
  table,
  td {
    mso-table-rspace: 0pt;
    mso-table-lspace: 0pt;
  }
  img {
    -ms-interpolation-mode: bicubic;
  }
  a[x-apple-data-detectors] {
    font-family: inherit !important;
    font-size: inherit !important;
    font-weight: inherit !important;
    line-height: inherit !important;
    color: inherit !important;
    text-decoration: none !important;
  }
  div[style*="margin: 16px 0;"] {
    margin: 0 !important;
  }

  body {
    width: 100% !important;
    height: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }
  table {
    border-collapse: collapse !important;
  }

  a {
    color: black;
  }

  img {
    height: auto;
    line-height: 100%;
    text-decoration: none;
    border: 0;
    outline: none;
  }
  </style>

</head>
<body style="background-color: #e9ecef;">
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td align="center" valign="top" style="padding: 36px 24px;">
              <a href="https://emcare.argusoft.com" target="_blank" rel="noopener noreferrer" style="display: inline-block;">
                <img src="https://emcare.argusoft.com/doc/resources/emcare_logo.png" alt="Logo" border="0" width="48" style="display: block; width: 48px; max-width: 48px; min-width: 48px;">
              </a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td bgcolor="#ffffff" align="left" style="padding: 24px; font-family: ''Source Sans Pro'', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
              <h1 style="margin: 0 0 12px; font-size: 32px; font-weight: 400; line-height: 48px;">Dear, {{firstName}} {{lastName}}!</h1>
              <p style="margin: 0;">Your registration request has been approved successfully.</p>
              <p style="margin: 0;">You can login with your email and password now.</p>
              <p style="margin: 0;">Thank You.</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
        <tr>
          <td align="center" valign="top" style="padding: 36px 24px;">
          </td>
        </tr>
      </table>
    </tr>
  </table>
</body>
</html>', '2022-05-25 10:42:35.547', 'emcare User Activated', 'firstName,lastName');
INSERT INTO public.email_content (id, code, content, created_at, subject, var_list) VALUES (4, 'CONFIRMATION_EMAIL_REJECTED', '<!DOCTYPE html>
<html>
<head>

  <meta charset="utf-8">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style type="text/css">
  @media screen {
    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 400;
      src: local(''Source Sans Pro Regular''), local(''SourceSansPro-Regular''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format(''woff'');
    }

    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 700;
      src: local(''Source Sans Pro Bold''), local(''SourceSansPro-Bold''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format(''woff'');
    }
  }
  body,
  table,
  td,
  a {
    -ms-text-size-adjust: 100%; /* 1 */
    -webkit-text-size-adjust: 100%; /* 2 */
  }
  table,
  td {
    mso-table-rspace: 0pt;
    mso-table-lspace: 0pt;
  }
  img {
    -ms-interpolation-mode: bicubic;
  }
  a[x-apple-data-detectors] {
    font-family: inherit !important;
    font-size: inherit !important;
    font-weight: inherit !important;
    line-height: inherit !important;
    color: inherit !important;
    text-decoration: none !important;
  }
  div[style*="margin: 16px 0;"] {
    margin: 0 !important;
  }

  body {
    width: 100% !important;
    height: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }
  table {
    border-collapse: collapse !important;
  }

  a {
    color: black;
  }

  img {
    height: auto;
    line-height: 100%;
    text-decoration: none;
    border: 0;
    outline: none;
  }
  </style>

</head>
<body style="background-color: #e9ecef;">
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td align="center" valign="top" style="padding: 36px 24px;">
              <a href="https://emcare.argusoft.com" target="_blank" rel="noopener noreferrer" style="display: inline-block;">
                <img src="https://emcare.argusoft.com/doc/resources/emcare_logo.png" alt="Logo" border="0" width="48" style="display: block; width: 48px; max-width: 48px; min-width: 48px;">
              </a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td bgcolor="#ffffff" align="left" style="padding: 24px; font-family: ''Source Sans Pro'', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
              <h1 style="margin: 0 0 12px; font-size: 32px; font-weight: 400; line-height: 48px;">Dear, {{firstName}} {{lastName}}!</h1>
              <p style="margin: 0;">Your registration request has not been approved. We regret the inconvenience caused.</p>
              <p style="margin: 0;">Thank You.</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
        <tr>
          <td align="center" valign="top" style="padding: 36px 24px;">
          </td>
        </tr>
      </table>
    </tr>
  </table>
</body>
</html>', '2022-05-26 10:42:35.547', 'emcare User Rejected', 'firstName,lastName');
INSERT INTO public.email_content (id, code, content, created_at, subject, var_list) VALUES (1, 'ADD_USER', '<!DOCTYPE html>
<html>
<head>

  <meta charset="utf-8">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style type="text/css">
  @media screen {
    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 400;
      src: local(''Source Sans Pro Regular''), local(''SourceSansPro-Regular''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format(''woff'');
    }

    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 700;
      src: local(''Source Sans Pro Bold''), local(''SourceSansPro-Bold''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format(''woff'');
    }
  }
  body,
  table,
  td,
  a {
    -ms-text-size-adjust: 100%; /* 1 */
    -webkit-text-size-adjust: 100%; /* 2 */
  }
  table,
  td {
    mso-table-rspace: 0pt;
    mso-table-lspace: 0pt;
  }
  img {
    -ms-interpolation-mode: bicubic;
  }
  a[x-apple-data-detectors] {
    font-family: inherit !important;
    font-size: inherit !important;
    font-weight: inherit !important;
    line-height: inherit !important;
    color: inherit !important;
    text-decoration: none !important;
  }
  div[style*="margin: 16px 0;"] {
    margin: 0 !important;
  }

  body {
    width: 100% !important;
    height: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }
  table {
    border-collapse: collapse !important;
  }

  a {
    color: black;
  }

  img {
    height: auto;
    line-height: 100%;
    text-decoration: none;
    border: 0;
    outline: none;
  }
  </style>

</head>
<body style="background-color: #e9ecef;">
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td align="center" valign="top" style="padding: 36px 24px;">
              <a href="https://emcare.argusoft.com" target="_blank" rel="noopener noreferrer" style="display: inline-block;">
                <img src="https://emcare.argusoft.com/doc/resources/emcare_logo.png" alt="Logo" border="0" width="48" style="display: block; width: 48px; max-width: 48px; min-width: 48px;">
              </a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td bgcolor="#ffffff" align="left" style="padding: 24px; font-family: ''Source Sans Pro'', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
              <h1 style="margin: 0 0 12px; font-size: 32px; font-weight: 400; line-height: 48px;">Welcome, {{firstName}} {{lastName}}!</h1>
              <p style="margin: 0;">Your registration request has been received successfully, An administrator shall soon approve it </p>
              <p style="margin: 0;">Thank You.</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
        <tr>
          <td align="center" valign="top" style="padding: 36px 24px;">
          </td>
        </tr>
      </table>
    </tr>
  </table>
</body>
</html>', '2022-05-06 10:42:35.547', 'emcare Registration', 'firstName,lastName');
INSERT INTO public.email_content (id, code, content, created_at, subject, var_list) VALUES (2, 'GENERATE_OTP', '<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style type="text/css">
  @media screen {
    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 400;
      src: local(''Source Sans Pro Regular''), local(''SourceSansPro-Regular''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format(''woff'');
    }
    @font-face {
      font-family: ''Source Sans Pro'';
      font-style: normal;
      font-weight: 700;
      src: local(''Source Sans Pro Bold''), local(''SourceSansPro-Bold''), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format(''woff'');
    }
  }
  body,
  table,
  td,
  a {
    -ms-text-size-adjust: 100%; /* 1 */
    -webkit-text-size-adjust: 100%; /* 2 */
  }
  table,
  td {
    mso-table-rspace: 0pt;
    mso-table-lspace: 0pt;
  }

  img {
    -ms-interpolation-mode: bicubic;
  }
  a[x-apple-data-detectors] {
    font-family: inherit !important;
    font-size: inherit !important;
    font-weight: inherit !important;
    line-height: inherit !important;
    color: inherit !important;
    text-decoration: none !important;
  }

  div[style*="margin: 16px 0;"] {
    margin: 0 !important;
  }

  body {
    width: 100% !important;
    height: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }
  table {
    border-collapse: collapse !important;
  }
  a {
    color: black;
  }

  img {
    height: auto;
    line-height: 100%;
    text-decoration: none;
    border: 0;
    outline: none;
  }
  </style>

</head>
<body style="background-color: #e9ecef;">
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td align="center" valign="top" style="padding: 36px 24px;">
              <a href="https://emcare.argusoft.com" target="_blank" rel="noopener noreferrer" style="display: inline-block;">
                <img src="https://emcare.argusoft.com/doc/resources/emcare_logo.png" alt="Logo" border="0" width="48" style="display: block; width: 48px; max-width: 48px; min-width: 48px;">
              </a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td align="center" bgcolor="#e9ecef">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
          <tr>
            <td bgcolor="#ffffff" align="left" style="padding: 24px; font-family: ''Source Sans Pro'', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;">
              <h1 style="margin: 0 0 12px; font-size: 32px; font-weight: 400; line-height: 48px;">One Time Password</h1>
              <p style="margin: 0;">Your one time password for reset password is 
                <p style="margin: 0 0 12px; font-size: 35px; font-weight: 600; line-height: 48px;">{{otp}}</p>
              </p>
            </td>
          </tr>
          <tr>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;">
        <tr>
          <td align="center" valign="top" style="padding: 36px 24px;">
          </td>
        </tr>
      </table>
    </tr>
  </table>
</body>
</html>', '2022-05-05 10:42:35.547', 'One Time Password For Reset Password', 'otp');



INSERT INTO public.settings (id, key, name, value) VALUES (1, 'REGISTRATION_EMAIL_AS_USERNAME', 'Registration email as username', 'Active');
INSERT INTO public.settings (id, key, name, value) VALUES (2, 'WELCOME_EMAIL', 'Welcome email', 'Active');
INSERT INTO public.settings (id, key, name, value) VALUES (3, 'SEND_CONFIRMATION_EMAIL', 'Send confirmation email', 'Active');

INSERT INTO public.language_translation(id, created_by, created_on, modified_by, modified_on, language_code, language_data, language_name) VALUES (2010, '34e85fd5-e155-4243-ab5e-a95ba4d60647', '2022-02-01 16:27:04.499', '34e85fd5-e155-4243-ab5e-a95ba4d60647', '2023-03-16 14:37:01.672', 'en', '"{"User list Heading":"All Users","Search Placeholder":"Search","Add User":"Add User","Update User":"Update User","Reset Password":"Reset Password","ID":"Sr. No","First Name":"First name","Last Name":"Last name","Email":"Email","Role":"Role","Location":"Location","Status":"Status","Verifed":"Verifed","Not Verified":"Not Verified","Password":"Password","Password Required Message":"*Password is required","ConfirmPassword":"Confirm Password","Confirm Password Required Message":"*Confirm Password is required","Passwords Match Message":"Passwords must match","FirstName Required Message":"*Firstname is required","LastName Required Message":"*LastName is required","Email Required Message":"*Email is required","Email Invalid Message":"*Email is invalid","Role Required Message":"*Role is required","Location Required Message":"*Location is required","FirstName Placeholder":"Enter first name","LastName Placeholder":"Enter last name","Email Placeholder":"Enter Email","Location Placeholder":"Enter Location","Password Placeholder":"Enter Password","ConfirmPassword Placeholder":"Enter confirm password","Roles":"Roles","Add Role":"Add Role","Name":"Name","Description":"Description","Update Role":"Update Role","Role Name":"Role Name","Role Name Placeholder":"Enter Role name","Role Name Required Message":"*Role name is required","Description Placeholder":"Enter description","Registration Requests":"Registration Requests","Approve User":"Approve User","Disappro veUser":"Disapprove User","Ok":"Ok","Cancel":"Cancel","Confirm Approval":"Are you sure you want to approve this user?","DisApproval":"Are you sure you want to disapprove this user?","Patients":"Patients","No":"No","Identifier":"Identifier","Gender":"Gender","Date of Birth":"Date of Birth","Caregiver Name":"Caregiver Name","Patient Details":"Patient Details","Given Name":"Given Name","Family Name":"Family Name","Caregiver":"Caregiver","Address":"Address","Postal Code":"Postal Code","Close":"Close","Show Location":"All Locations","Add Location":"Add Location","Location Name":"Location Name","Type":"Type","Parent":"Parent","Update Location":"Update Location","Location Type":"Location Types","Location Type Required Message":"*Location type is required","Location Name Placeholder":"Location Name","Location Name Required Message":"*Location name is required","Parent Placeholder":"Enter parent","Add Location Type":"Add Location Type","Update Location Type":"Update Location Type","Location Type Placeholder":"Enter type","Name Placeholder":"Enter name","Name Required Message":"*Location type name is required","Device UUID":"Device UUID","Android Version":"Android Version","Logged in User":"Logged in User","App Usage":"App Usage","Features":"Feature","Feature Name":"Feature name","UpdateFeatureConfigUpdate Feature Config":"Update Feature Config","User":"User","ADD":"ADD","Filter":"Filter","Location Filter":"Location Filter","Country":"Country","State":"State","City":"City","Region":"Region","Other":"Other","Get Results":"Get Results","Logout":"Logout","User Management":"User Management","Users":"Users","Registration Request":"Registration Request","Manage Patients":"Patients","Manage Locations":"Locations","Manage Devices":"Devices","Manage Features":"Features","No Records":"No Records Found!!","Submit":"Submit","Back":"Back","Edit Profile":"Edit Profile","Save Details":"Save Details","Language":"Language","French":"French","English":"English","Hindi":"Hindi","Manage Translations":"Manage Translations","Save Changes":"Save Changes","Registration":"Registration","Risk_Assessment":"Risk Assessment","Referral":"Referral","Notification":"Notification","Reports":"Reports","Announcements":"Announcements","Welcome":"Welcome","Home_title":"What would you like to do today!","Add_Patient":"Add Patient","Find_by_Patient_Name":"Find by Patient Name","Delete_Patient":"Delete Patient","Select_Location":"Select Location","Select":"Select","Edit_Profile":"Edit Profile","Settings":"Settings","Select_Language":"Select Language","Registered_Patients":"Registered Patients","Patient_Details":"Patient Details","Please_select_location":"Please select location","Sync_started":"Sync started","Sync_Successful":"Sync Successful","Sync_failed":"Sync failed","Questionnaire Management":"Questionnaire","Manage Facility":"Facility","Show Facility":"Show Facility","Save Facility":"Save Facility","Edit Facility":"Edit Facility","Add Facility":"Add Facility","Facilities":"Facility","Compare Patients":"Compare Patients","Questionnaires":"Questionnaires","Add Questionnaire":"Add Questionnaire","Title":"Title","Questionnaire ID":"Questionnaire ID","Questionnaire Builder":"Questionnaire Builder","Update Questionnaire":"Update Questionnaire","Locations":"Locations","Device List":"Devices","Launguage":"Language","Add Launguage":"Add language","No Translations":"There are no translations with the selected alphabet!!","Address Street":"Address Street","Save":"Save","Name is required":"Name is required","Address street is required":"Address street is required","Status is required":"Status is required","Telecom is required":"Telecom is required","First Patient":"First Patient","Upload first Patient":"Upload first Patient","Second Patient":"Second Patient","Upload Second Patient":"Upload Second Patient","First Patient JSON":"First Patient JSON","First Patient Details":"First Patient Details","Patient data is required":"Patient data is required","Second Patient JSON":"Second Patient JSON","Second Patient Details":"Second Patient Details","Compare":"Compare","Select Launguage":"Select language","Select country":"Select country","Select Location":"Select Location","Languages":"Languages","Date Created":"Date Created","Action":"Action","Manage Language":"Language","Username":"Username","Username Placeholder":"Enter Username","Username Required Message":"*Username is required","Manage Settings":"User Settings!","RegisterEmail":"Register email as username?","WelcomeEmail":"Send Welcome email for user registration?","Subject":"Subject","Content":"Content","ConfirmationEmail":"Send confirmation email for user registration?","Total Patients":"Total Patients","Total Users":"Total Users","Pending User Requests":"Pending User Requests","Analysis":"Analytics","Map":"Map","Dashboard":"Dashboard","Previous":"Previous","Next":"Next","Devices":"Devices","Facility":"Facility","Feature":"Feature","All Locations":"All Locations","Location Types":"Location Types","All Patient":"All Patient","User Settings":"User Settings","All Users":"All Users","Manage Organization":"Manage Organization","Alias Name":"Alias Name","Alias is required":"Alias is required","Edit Organization":"Edit Organization","Save Organization":"Save Organization","Organizations":"Organizations","Add Organization":"Add Organization","Alias":"Alias","Telecom":"Telecom","Organization":"Organization","Organization is required":"Organization is required","Latitude":"Latitude","Longitude":"Longitude","Altitude":"Altitude","Latitude is required":"Latitude is required","Longitude is required":"Longitude is required","Altitude is required":"Altitude is required","Detect Current Location":"Detect Current Location","Detect Location":"Detect Location","Administrative Location":"Administrative Location","PhoneNumber":"Phone Number","Phone Placeholder":"Enter phone number","Phone Required Message":"*Phone number is required","Phone Invalid Message":"Please enter valid phone number","Sr. No":"Sr. No","Advanced settings":"Advanced settings","NoAccess":"Sorry you don''t have access to this page!!","Key Performance Indicators":"Key Performance Indicators","Last sync":"Last sync","Consultation":"Consultation","Select Encounter Key":"Select Encounter","Code":"Code","Code Description":"Code Description","Add Code":"Add Code","Update Code":"Update Code","Code Name":"Code Name","CodePlaceholder":"Enter Code","Code Required Message":"*Code is required","CodeDescriptionPlaceholder":"Enter Code Description","Code Description Required Message":"*Code Description is required","Indicator":"Indicator","Code Name Placeholder":"Enter Code Name","Code Name Required Message":"*Code Name is required","Indicator Name":"Indicator Name","Indicator Name Placeholder":"Enter Indicator Name","Indicator Name Required Message":"*Indicator Name is required","Indicator Description":"Indicator Description","Indicator Description Required Message":"*Indicator Description is required","Numerator":"Numerator","Denominator":"Denominator","DST":"Display Type","Identifier Placeholder":"Enter Equation Identifier","Value":"Value","value Placeholder":"Enter Value","Condition":"Condition","Value Type":"Value Type","Numerator Equation":"Numerator Equation","Numerator Equation Placeholder":"Enter Numerator Equation","Numerator Equation Required Message":"*Numerator Equation is Required","Denominator Equation":"Denominator Equation","Denominator Equation Placeholder":"Enter Denominator Equation","Denominator Equation Required Message":"*Denominator Equation is Required","Add Indicator":"Add Indicator","Indicator Code":"Indicator Code","Display Type":"Display Type","Update":"Update","Data Fields":"Data Fields","Equation":"Equation","Add Tenant":"Add Country","Update Tenant":"Update Country","Tenant Id Placeholder":"Enter Country Id","Tenant Id Required Message":"*Country is Required","URL Placeholder":"Ex. 127.0.0.1:5432/{database_name}","URL Required Message":"*URL is Required","Domain Placeholder":"Enter Country Domain","Domain Required Message":"*Country Domain in Required","Tenants":"Countries","URL":"Database URL","Tenant Id Repeat Message":"Please enter different Country!!","URL Repeat Message":"Please enter different URL!!","Domain Repeat Message":"Please enter different Country Domain!!","Ig Version":"Ig Version","Domain":"Country","Language Required Message":"*Language is Required","Administrative Location Type":"Administrative Location Type","Facility Name":"Facility Name"}"', 'English');

INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (18, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Compare Patients', '3', 5);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (20, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Duplicate Patients', '3', 6);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (2, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Locations', NULL, 7);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (9, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Health facilities', '2', 8);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (16, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Administrative Levels', '2', 9);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (15, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Location Types', '2', 10);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (7, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Questionnaires', NULL, 11);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (1, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Users', NULL, 12);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (14, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Dashboard', NULL, 1);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (10, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'All Users', '1', 13);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (4, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Features', '21', 20);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (5, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Roles', '1', 14);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (6, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Devices', '21', 21);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (8, 'EM CARE SYSTEM', '2022-03-01 12:33:23.940559', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Languages', '21', 23);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (12, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'User Settings', '21', 22);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (19, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Organizations', NULL, 24);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (11, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Registration Request', '1', 15);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (21, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Advanced settings', NULL, 19);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (3, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Patients', NULL, 2);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (23, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Indicators', NULL, 16);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (17, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'All Patient', '3', 3);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (24, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'All Indicators', '23', 17);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (25, 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', 'EM CARE SYSTEM', '2021-12-30 09:00:17.219', '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Custom Codes', '23', 18);
INSERT INTO public.menu_config (id, created_by, created_on, modified_by, modified_on, feature_json, is_active, menu_name, parent, order_number) VALUES (22, 'EM CARE SYSTEM', '2022-03-14 05:33:56.439836', NULL, NULL, '{"canAdd":true,"canEdit":true,"canView":true,"canDelete":true}', 'true', 'Consultations', '3', 4);


SELECT pg_catalog.setval('public.hibernate_sequence', 1, true);


ALTER TABLE ONLY public.activity_definition_resource
    ADD CONSTRAINT activity_definition_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.code_system_resource
    ADD CONSTRAINT code_system_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.condition_resource
    ADD CONSTRAINT condition_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.device_master
    ADD CONSTRAINT device_master_pkey PRIMARY KEY (device_id);


ALTER TABLE ONLY public.email_content
    ADD CONSTRAINT email_content_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.emcare_custom_code_system
    ADD CONSTRAINT emcare_custom_code_system_pkey PRIMARY KEY (code_id);


ALTER TABLE ONLY public.emcare_resources
    ADD CONSTRAINT emcare_resources_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.encounter_resource
    ADD CONSTRAINT encounter_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.hierarchy_master
    ADD CONSTRAINT hierarchy_master_pkey PRIMARY KEY (hierarchy_type);


ALTER TABLE ONLY public.indicator_denominator_equation
    ADD CONSTRAINT indicator_denominator_equation_pkey PRIMARY KEY (denominator_id);


ALTER TABLE ONLY public.indicator_numerator_equation
    ADD CONSTRAINT indicator_numerator_equation_pkey PRIMARY KEY (numerator_id);


ALTER TABLE ONLY public.indicator
    ADD CONSTRAINT indicator_pkey PRIMARY KEY (indicator_id);


ALTER TABLE ONLY public.language_translation
    ADD CONSTRAINT language_translation_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.library_resource
    ADD CONSTRAINT library_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.location_master
    ADD CONSTRAINT location_master_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.location_resources
    ADD CONSTRAINT location_resources_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.medication_resource
    ADD CONSTRAINT medication_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.menu_config
    ADD CONSTRAINT menu_config_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.observation_resource
    ADD CONSTRAINT observation_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.operation_definition_resource
    ADD CONSTRAINT operation_definition_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.organization_resources
    ADD CONSTRAINT organization_resources_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.otp
    ADD CONSTRAINT otp_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.plan_definition_resources
    ADD CONSTRAINT plan_definition_resources_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.questionnaire_master
    ADD CONSTRAINT questionnaire_master_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.questionnaire_response
    ADD CONSTRAINT questionnaire_response_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.related_person_resource
    ADD CONSTRAINT related_person_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.structure_definition_resource
    ADD CONSTRAINT structure_definition_resource_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.structure_map_resource
    ADD CONSTRAINT structure_map_resource_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.tutorials
    ADD CONSTRAINT tutorials_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.user_location_mapping
    ADD CONSTRAINT user_location_mapping_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.user_master
    ADD CONSTRAINT user_master_pkey PRIMARY KEY (user_id);


ALTER TABLE ONLY public.user_menu_config
    ADD CONSTRAINT user_menu_config_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.value_set_resources
    ADD CONSTRAINT value_set_resources_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.indicator_denominator_equation
    ADD CONSTRAINT fk84nmcxo3vxussqkvmqvfw2ooi FOREIGN KEY (indicator_id) REFERENCES public.indicator(indicator_id);


ALTER TABLE ONLY public.indicator_numerator_equation
    ADD CONSTRAINT fka8fg2m4wrqnvbrk6fe8fvkmq4 FOREIGN KEY (indicator_id) REFERENCES public.indicator(indicator_id);