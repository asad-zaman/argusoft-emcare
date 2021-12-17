import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Bundle } from "fhir/r4";

@Injectable({
    providedIn: 'root'
})
export class RoleManagementService {

    roleBaseURL = `http://localhost:8080/api/role`

    constructor(private http: HttpClient) { }

    getHeaders() {
        const headerObj = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
                'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3UjNtcWZXbVlGUFRUZTNCQUZuaUxCSWhGT3RTaUVmV0lHUVA0VjBoRFlVIn0.eyJleHAiOjE2Mzk3NzQ0NTAsImlhdCI6MTYzOTczODQ1MCwianRpIjoiMzU3NWJlZWYtMjQyNy00ODFmLTk0M2MtZGVmODcwYjQ5OWJiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZV9kZW1vIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6ImM4NGM1YjYyLTExMTUtNDcyZi04N2QxLWY0YzAxMDY2MTQ4NSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImVtY2FyZV9jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZjYwYzFlYzktN2M0MS00NTJlLTgwMTMtOGNiMDQ4MjhmMGI2IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiLCJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtZW1jYXJlLWRlbW8iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJyZWFsbS1hZG1pbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiZW1jYXJlX2NsaWVudCI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJmNjBjMWVjOS03YzQxLTQ1MmUtODAxMy04Y2IwNDgyOGYwYjYiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QifQ.jDapKBHi0-f328kM20V4hUWKasybkCLKZFRmCkB4bv14O3O9HiAR1GQVPOBIJHFwqWle0abf7jx_0bYUSSXIEoU21_QVP4SHCwzyBQBd7bGDmuPxPr9aVqLZtlWGvqNe7a6CexGXOlKVAus6dm5Rzq8LUDTteCa29M7WV-Yqbj_KzTFS3C_MRUYJVS81OZNl6IjsMXG4gO6c87q3nLOc-wVFql2cRcT63MhJ0sxvAyxx9wDhtnav-YG_kdRUKvdKxGudvZrbUB_9NAtmmDhAiVohxhJ10nRTpZDA6eY5lM4JGkZABSSjUIuSjB_1cFKkJ3UScrX105pTKWwF_aL4zA'
            })
        };
        return headerObj;
    }

    //Endpoints TODO

    getAllRoles() {
        return this.http.get(`${this.roleBaseURL}`, this.getHeaders());
    }

    getRoleById(id:String) {
        return this.http.get(`${this.roleBaseURL}/${id}`, this.getHeaders());
    }

    createRole(obj) {
        return this.http.post(`${this.roleBaseURL}/add`, obj, this.getHeaders());
    }

    updateRole(obj) {
        return this.http.put(`${this.roleBaseURL}/update`, obj, this.getHeaders());
    }

}