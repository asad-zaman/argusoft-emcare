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
            'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3UjNtcWZXbVlGUFRUZTNCQUZuaUxCSWhGT3RTaUVmV0lHUVA0VjBoRFlVIn0.eyJleHAiOjE2Mzk2NzAxOTQsImlhdCI6MTYzOTYzNDE5NCwianRpIjoiYjliMDdiMGItYjA0Zi00NWE0LWJlYmUtMzg2NjM0M2QxOGRmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZV9kZW1vIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6ImM4NGM1YjYyLTExMTUtNDcyZi04N2QxLWY0YzAxMDY2MTQ4NSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImVtY2FyZV9jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZmEwNzFkNjAtYmI5Ni00MzA4LTkyOTgtMGY4YzY1MjQzZmE0IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiLCJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtZW1jYXJlLWRlbW8iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJyZWFsbS1hZG1pbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiZW1jYXJlX2NsaWVudCI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJmYTA3MWQ2MC1iYjk2LTQzMDgtOTI5OC0wZjhjNjUyNDNmYTQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QifQ.kEp-EfT8rVs8_vfbyuSPGfXVfefagc14fN7De3cABjCpHyD7KT8RaCOcaZtVyJdyvWlG8ExA0R9l2f2g9IJ8U4enVyu429sUs79d-cqbiBunP_VLklAoX0cDb6hW1rzG4DWZg45HoINZcCVScP3xzpfTR5MKvOFRcc_bm3UxocfEILx5xcSeLr7pwC_9fsocSDjjXSeW5thwFUdcew8_y97XydKQ7mbneXk-MENsPhk4-QGS0dEcJW82BjAmykVrpv_3zardJjt1kEweymm23VSWYe5jB5yYScE4xJ6mx-DLMMyyknpgFOD9vSgRUuBSCafE_RnDxZO3hsk32yG0AA'
        })
        };
        return headerObj;
    }

    //Endpoints
    getAllPatients() {
        return this.http.get<Bundle>(`${this.fhirBaseURL}/patient`,this.getHeaders());
    }
}