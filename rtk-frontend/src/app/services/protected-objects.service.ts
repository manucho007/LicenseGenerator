import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProtectedObjectsService {

  constructor() { }

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

    if (leafObject["data"].hasOwnProperty("name")) {
      protectedObject["name"] = leafObject["data"]["name"];
    }

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
        if (object.hasOwnProperty("name")) {
          protectedObject.push({"data": object["data"], "name": object["name"]});
        }
        else {
          protectedObject.push({"data": object["data"]});
        }
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

      if (object.hasOwnProperty("name")) {
        if (!protectedObject.hasOwnProperty("name")) {
          protectedObject["name"] = object["name"];
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

  recursiveFindProtectedObject(protectedObject, protectedObjects) {
    if (protectedObjects != null) {
      for (let i = 0; i < protectedObjects.length; i++) {
        if (Array.isArray(protectedObject)) {
          for (let j = 0; j < protectedObject.length; j++) {
            if (protectedObject[j].data === protectedObjects[i].data && protectedObject[j].name === protectedObjects[i].name) {
              if (protectedObject[j].hasOwnProperty("children") && protectedObjects[i].hasChildren) {
                return this.recursiveFindProtectedObject(protectedObject[j].children, protectedObjects[i].children);
              }
              else {
                if ((protectedObjects[i].hasChildren && !protectedObject[j].hasOwnProperty("children")) || (!protectedObjects[i].hasChildren && protectedObject[j].hasOwnProperty("children"))) {
                  return -1;
                }
                else {
                  return protectedObjects[i].id;
                }
              }
            }
          }
        }
        else {
          if (protectedObject.data === protectedObjects[i].data && protectedObject.name === protectedObjects[i].name) {
            if (protectedObject.hasOwnProperty("children") && protectedObjects[i].hasOwnProperty("children")) {
              return this.recursiveFindProtectedObject(protectedObject.children, protectedObjects[i].children);
            }
            else {
              if ((protectedObjects[i].hasOwnProperty("children") && !protectedObject.hasOwnProperty("children")) || (!protectedObjects[i].hasOwnProperty("children") && protectedObject.hasOwnProperty("children"))) {
                return -1;
              }
              else {
                return protectedObjects[i].id;
              }
            }
          }
        }
      }
    }

    return -1;
  }
}
