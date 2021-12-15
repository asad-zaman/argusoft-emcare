import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Bundle } from "fhir/r4";

@Injectable({
    providedIn:'root'
})
export class FhirService {

    fhirBaseURL= `http://localhost:8080/fhir`

    constructor(private http: HttpClient){ }

    getHeaders() {
        const headerObj = {
        headers: new HttpHeaders({
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Headers': 'Content-Type',
            'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
            'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3UjNtcWZXbVlGUFRUZTNCQUZuaUxCSWhGT3RTaUVmV0lHUVA0VjBoRFlVIn0.eyJleHAiOjE2Mzk1ODI1NzksImlhdCI6MTYzOTU0NjU3OSwianRpIjoiZjcwYjBhOWMtMDFjNC00YzVlLTg3YjctNzg2NTVhNzdhZTNlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZV9kZW1vIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6ImM4NGM1YjYyLTExMTUtNDcyZi04N2QxLWY0YzAxMDY2MTQ4NSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImVtY2FyZV9jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiY2FiZTVkMTEtODJjYS00NTcyLWE4NzEtNzU4YzU2YTRmM2I2IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiLCJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtZW1jYXJlLWRlbW8iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJyZWFsbS1hZG1pbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiZW1jYXJlX2NsaWVudCI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJjYWJlNWQxMS04MmNhLTQ1NzItYTg3MS03NThjNTZhNGYzYjYiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QifQ.enb235ZCAjgLOqKtbX_I_NkSahGOb3hzm3Gby1CUDVnNtlLahvGUiqyB5D8C8RFJktBwCimg-7jF_l1iglQLbkZo0jSwhaWr3iZnQ3ozxj9r40SVuPYfmZ9vwyfzp_8YfYN2w74ScE3e3gEK1kp1oLAmCDCrFvY8O21CPb9vS7k40bQYjz03pYtHRCpAqe5shj0GPDzBLUa6hUyRM7WLzv48_XWnbk4JD_T-T5uw6k_QBZgHIEl2t4jWz95OcihDb-Dk9TgNcfO-OAtvd5P-IrkL3OhAO11sgBNyXkGbX3Jd7i9stQShOFf4ErXcDmJ4pk3TqzH5eCCMcfqNVI92wQ'
        })
        };
        return headerObj;
    }

    //Endpoints
    getAllPatients() {
        return this.http.get<Bundle>(`${this.fhirBaseURL}/Patient`,this.getHeaders());
    }
}