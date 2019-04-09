import { Component, OnInit } from "@angular/core";
import { RestService } from "src/app/services/rest.service";
import { FormBuilder, FormGroup, FormArray, FormControl } from '@angular/forms';

import { ProtectedObject } from "src/app/interfaces/protectedObjects";



@Component({
  selector: "app-generator",
  templateUrl: "./generator.component.html",
  styleUrls: ["./generator.component.scss"]
})

export class GeneratorComponent implements OnInit {

  protectedObj: any;

  form:FormGroup;
  // Test Data
  orders = [
  { id: 1, name: 'order 1' },
  { id: 2, name: 'order 2' },
  { id: 3, name: 'order 3' },
  { id: 4, name: 'order 4' }
];
  // Interface imported
  licenseDoc: ProtectedObject = {
    beginDate: "",
    endDate: "",
    protectedObjects: [
    ]
  };

  constructor(
    private rest: RestService,
    private formBuilder:FormBuilder
    ) {
      this.form = this.formBuilder.group({
        orders: new FormArray([])
      });
         this.addCheckboxes();

    }
  
 
    private addCheckboxes() {
      this.orders.map((o, i) => {
        const control = new FormControl(i === 0); // if first item set to true, else false
        (this.form.controls.orders as FormArray).push(control);
      });
    }
  
    submit() {
      console.log(this.form.value);  
    }
  
  
 

  ngOnInit() {
    this.loadObjects();
  }









  // Get Protected objects
   loadObjects() {
    return this.rest.getObjects().subscribe((data: {}) => {
      //console.log(data);
      let mydata = data;
      let result = mydata[0].children[0].children; 
      console.log(mydata[0].children[0].children);

      this.protectedObj = data;
      console.log(this.protectedObj);
    });
  }
  // Function to post the license
  generateLicense() {
    // this.rest.generateLicense(licenseDoc);
      // console.log(this.licenseDoc);
  }


}
