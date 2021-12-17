import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Bundle } from "fhir/r4";

@Injectable({
    providedIn:'root'
})
export class FhirService {

    fhirBaseURL= `http://localhost:8080/api/emcare`

    constructor(private http: HttpClient){ }

    getHeaders() {
        const headerObj = {
        headers: new HttpHeaders({
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Headers': 'Content-Type',
            'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
            'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3UjNtcWZXbVlGUFRUZTNCQUZuaUxCSWhGT3RTaUVmV0lHUVA0VjBoRFlVIn0.eyJleHAiOjE2Mzk3NTIyNzEsImlhdCI6MTYzOTcxNjI3MSwianRpIjoiNTM0NjU5MWUtNDU1OS00ZDVmLWI4ODItNGZlZGM4OTFjNTY1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZV9kZW1vIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6ImM4NGM1YjYyLTExMTUtNDcyZi04N2QxLWY0YzAxMDY2MTQ4NSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImVtY2FyZV9jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNTAyMzlkZTMtMzg3ZS00NWYwLThkNzQtZTE2MjFjNmZkZjIwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiLCJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtZW1jYXJlLWRlbW8iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJyZWFsbS1hZG1pbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiZW1jYXJlX2NsaWVudCI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI1MDIzOWRlMy0zODdlLTQ1ZjAtOGQ3NC1lMTYyMWM2ZmRmMjAiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QifQ.pgS8-Dn2nsR2LgxcP2wVEMDzbklmCz_tGwdx5M9PJrqOODh40s9hwTBAUq9s2VJoWyC8D_IgOSqtJ4yIrAXu1Soc5rWeH-xIpYZZWVbYc3IX54RZ3EpADa9zk8wrXFpIEs0vd6Bz--8p3l_0VR6Zlbb8yrEYggATf9nqHgMNX3XZJrowqg1Z8A5aoXelMK8e2wCMuVjvbghXY8bEgKhy9_qT4xWi6cll4QhbjqSgBB5R2VhLjdCrwHnWkaFC-LX_308Ckx8x1c_HiE33KkI8IsDHC9onq7GtJmzvr4uadTNfYu4QFJHVkSjVB8viljeufF7yRg4PiLz8TjbOJ3uSNg'
        })
        };
        return headerObj;
    }

    //Endpoints
    getAllPatients() {
        return this.http.get(`${this.fhirBaseURL}/patient`,this.getHeaders());
    }
}