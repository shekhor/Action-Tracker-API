update action_status set toggled_on = 'YES' where id = ?;

//Fields : id, status_name, description, status, type, toggled_on
//type : SYSTEM_DEFINED, USER_DEFINED
//status : ACTIVE/INACTIVE