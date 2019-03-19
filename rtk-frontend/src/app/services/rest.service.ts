import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';


// Declaration of RESTful API endpoint and HTTP header
const endpoint = 'http://localhost:8090/api/protected-objects';
const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class RestService {
  constructor(private http: HttpClient) {  }

  // Function extracts the response
  private extractData(res: Response) {
    let body = res;
    return body || { };
  }
 
  // Function to GET the list of apps
  getApps(): Observable<any> {
    return this.http.get(endpoint + 'apps').pipe(
      map(this.extractData));
  }

  // Function to handle errors
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
  
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
  
      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);
  
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
