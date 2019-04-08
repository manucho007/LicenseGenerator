import { Injectable } from "@angular/core";
import {
  HttpClient,
  HttpHeaders,
  HttpResponse
} from "@angular/common/http";
import { Observable, of, throwError } from "rxjs";
import { map, catchError, tap, retry } from "rxjs/operators";

// Declaration of RESTful API endpoint and HTTP header
const getEndpoint = "api/protected-objects";
const postEnpoint = "api/generate-license";

// Http Options
const httpOptions = {
  headers: new HttpHeaders({
    "Content-Type": "application/json"
  }),
  responseType: 'arraybuffer' as 'json',
  observe: 'response' as 'body'
};

@Injectable({
  providedIn: "root"
})
export class RestService {
  constructor(private http: HttpClient) {}

  // Function to get the objects
  // HttpClient API get() method => Fetch Protected Objects
  getObjects(): Observable<any> {
    // return this.http.get<any>(getEndpoint)
    return this.http.get<any>("api/protected-objects").pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  // Function to generate the license
  // HttpClient API post() method => Generate License
  generateLicense(protectedObjects): Observable<HttpResponse<any>> {
    // return this.http.post<any>(postEnpoint, JSON.stringify(protectedObject),httpOptions)
    return this.http
      .post<any>(
        "api/generate-license",
        JSON.stringify(protectedObjects),
        httpOptions
      )
      .pipe(
        retry(1),
        catchError(this.handleError)
      );
  }

  // Error handling
  handleError(error) {
    let errorMessage = "";
    if (error.error instanceof ErrorEvent) {
      // Get client-side error
      errorMessage = error.error.message;
    } else {
      // Get server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.log(errorMessage);

    // window.alert(errorMessage);
    return throwError(errorMessage);
  }
}
