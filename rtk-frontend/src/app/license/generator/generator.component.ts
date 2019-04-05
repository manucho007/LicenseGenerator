import { Component, OnInit } from "@angular/core";
import { RestService } from "src/app/services/rest.service";

@Component({
  selector: "app-generator",
  templateUrl: "./generator.component.html",
  styleUrls: ["./generator.component.scss"]
})
export class GeneratorComponent implements OnInit {
  constructor(private rest: RestService) {}

  ngOnInit() {
    this.loadObjects();
  }

  // Get Protected objects
  loadObjects() {
    return this.rest.getObjects().subscribe((data: {}) => {
      console.log(data);
    });
  }
}
