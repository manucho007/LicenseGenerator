import {Component, Input, OnInit} from '@angular/core';
import {RestService} from "../services/rest.service";
import {ProtectedObjectsComponent} from "../protected-objects/protected-objects.component";
import {DatesComponent} from "../dates/dates.component";
import * as FileSaver from "file-saver";
import {BlockUI, NgBlockUI} from "ng-block-ui";

@Component({
  selector: 'app-actions',
  templateUrl: './actions.component.html',
  styleUrls: ['./actions.component.scss']
})
export class ActionsComponent implements OnInit {

  constructor(private rest: RestService) {}

  @Input() protectedObjects: ProtectedObjectsComponent;
  @Input() dates: DatesComponent;

  @BlockUI() blockUI: NgBlockUI;

  ngOnInit() {}

  recursiveAssign(obj, stack) {
    let value = stack.pop();

    if (value != null) {
      obj["children"] = value;

      this.recursiveAssign(obj["children"], stack);
    }
  }

  genProtectedObject(leafObject, stack) {
    let protectedObject = {};

    protectedObject["data"] = leafObject["data"]["data"];

    stack.push(protectedObject);

    if (leafObject.isRoot != true) {
      let parentProtectedObject = this.genProtectedObject(leafObject.parent, stack);

      stack.push(parentProtectedObject);
    }

    let arr = [];

    protectedObject = stack.pop();

    let arraySize = stack.length;

    while(arraySize--) arr.push("children");

    this.recursiveAssign(protectedObject, stack);

    return protectedObject;
  }

  findProtectedObject(protectedObject, object) {
    for (let i = 0; i < protectedObject.length; i++) {
      if (protectedObject[i]["data"] === object["data"]) {
        return true;
      }
    }

    return  false;
  }

  addProtectedObject(protectedObject, object) {
    if (Array.isArray(protectedObject)) {
      if (!this.findProtectedObject(protectedObject, object)) {
        protectedObject.push({"data": object["data"]});
      }

      let i;

      for (i = 0; i < protectedObject.length; i++) {
        if (protectedObject[i]["data"] === object["data"]) {
          if (object.hasOwnProperty("children")) {
            if (!protectedObject[i].hasOwnProperty("children")) {
              protectedObject[i]["children"] = [];
            }

            protectedObject[i]["children"] = this.addProtectedObject(protectedObject[i]["children"], object["children"]);
          }
        }
      }
    }
    else {
      if (object.hasOwnProperty("data")) {
        if (!protectedObject.hasOwnProperty("data")) {
          protectedObject["data"] = object["data"];
        }
      }

      if (object.hasOwnProperty("children")) {
        if (protectedObject.hasOwnProperty("children")) {
            protectedObject["children"] = this.addProtectedObject(protectedObject["children"], object["children"]);
        }
        else {
          protectedObject["children"] = [];

          protectedObject["children"].push(this.addProtectedObject({}, object["children"]));
        }
      }
    }

    return protectedObject;
  }

  addProtectedObjects(protectedObjects, object) {
    if (protectedObjects.hasOwnProperty(object.data)) {
      const protectedObject = protectedObjects[object.data];
      const returnProtectedObject = this.addProtectedObject(protectedObject, object);

      protectedObjects[object.data] = returnProtectedObject;
    }
    else {
      protectedObjects[object.data] = {};

      protectedObjects = this.addProtectedObjects(protectedObjects, object);
    }

    return protectedObjects;
  }

  onClickGenerate() {
    let protectedObjects = this.protectedObjects.getTreeComponent().treeModel.selectedLeafNodes;

    let returnProtectedObjects = {};

    let protectedObject;

    for (let i = 0; i < protectedObjects.length; i++) {
      const leafObject = protectedObjects[i];

      protectedObject = this.genProtectedObject(leafObject, []);

      returnProtectedObjects = this.addProtectedObjects(returnProtectedObjects, protectedObject);
    }

    let beginDate = this.dates.getBeginDate();
    let endDate = this.dates.getEndDate();

    let postProtectedObjects = {};

    postProtectedObjects["beginDate"] = beginDate;
    postProtectedObjects["endDate"] = endDate;
    postProtectedObjects["protectedObjects"] = {};
    postProtectedObjects["protectedObjects"]["objects"] = returnProtectedObjects;

    console.log(JSON.stringify(postProtectedObjects));

    return this.rest.generateLicense(postProtectedObjects).subscribe(data => {
      const contentDispositionHeader = data.headers.get("content-disposition");

      const fileNameIndex = contentDispositionHeader.indexOf("filename=") + 9;

      const fileName = contentDispositionHeader.substring(fileNameIndex);

      const file = new File([data.body], fileName, {type: "application/zip"});

      FileSaver.saveAs(file);
    });
  }

  onClickUpdate() {
    this.blockUI.start('Loading...');

    return this.rest.updateList().subscribe(data => {
      this.blockUI.stop();
    });
  }
}
