import {Component, Input, OnInit} from '@angular/core';
import {RestService} from "../services/rest.service";
import {ProtectedObjectsComponent} from "../protected-objects/protected-objects.component";
import {DatesComponent} from "../dates/dates.component";
import * as FileSaver from "file-saver";
import {BlockUI, NgBlockUI} from "ng-block-ui";
import {ProtectedObjectsService} from "../services/protected-objects.service";

@Component({
  selector: 'app-actions',
  templateUrl: './actions.component.html',
  styleUrls: ['./actions.component.scss']
})
export class ActionsComponent implements OnInit {

  constructor(private rest: RestService, private protectedObjectService: ProtectedObjectsService) {}

  @Input() protectedObjects: ProtectedObjectsComponent;
  @Input() dates: DatesComponent;

  @BlockUI() blockUI: NgBlockUI;

  ngOnInit() {}

  onClickGenerate() {
    let protectedObjects = this.protectedObjects.getTreeComponent().treeModel.selectedLeafNodes;

    if (protectedObjects.length == 0) {
      return;
    }

    let returnProtectedObjects = {};

    let protectedObject;

    for (let i = 0; i < protectedObjects.length; i++) {
      const leafObject = protectedObjects[i];

      protectedObject = this.protectedObjectService.genProtectedObject(leafObject, []);

      returnProtectedObjects = this.protectedObjectService.addProtectedObjects(returnProtectedObjects, protectedObject);
    }

    let beginDate = this.dates.getBeginDate();
    let endDate = this.dates.getEndDate();

    let postProtectedObjects = {};

    postProtectedObjects["beginDate"] = beginDate;
    postProtectedObjects["endDate"] = endDate;
    postProtectedObjects["protectedObjects"] = {};
    postProtectedObjects["protectedObjects"]["objects"] = returnProtectedObjects;

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

    this.rest.updateList().subscribe(data => {
      const protectedObjects = this.protectedObjects.getTreeComponent().treeModel.selectedLeafNodes;

      let returnProtectedObjects = [];

      if (protectedObjects.length != 0) {
        let protectedObject;

        for (let i = 0; i < protectedObjects.length; i++) {
          const leafObject = protectedObjects[i];

          protectedObject = this.protectedObjectService.genProtectedObject(leafObject, []);

          returnProtectedObjects.push(protectedObject);
        }

        this.protectedObjects.setSavedState(returnProtectedObjects);
      }
      else {
        this.protectedObjects.setSavedState(returnProtectedObjects);
      }

      this.protectedObjects.clearNodes();

      this.protectedObjects.getTreeComponent().treeModel.setState({});

      const self = this;

      Object.keys(data).map(function (objectKeyElement) {
        const mapKeys = data[objectKeyElement];

        Object.keys(mapKeys).map(function (mapKeyElement) {
          const value = mapKeys[mapKeyElement];

          self.protectedObjects.getNodes().push(value);
        });
      });

      this.blockUI.stop();
    },
    e => {
      this.blockUI.stop();
    });
  }
}
