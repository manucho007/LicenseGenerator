import {Component, OnInit, ViewChild} from '@angular/core';
import {ITreeOptions, TreeComponent, TreeModel, TreeNode} from "angular-tree-component";
import { TREE_ACTIONS, IActionMapping } from 'angular-tree-component';
import {RestService} from "../services/rest.service";
import { fromEvent } from 'rxjs';
import {debounceTime} from "rxjs/operators";
import {ProtectedObjectsService} from "../services/protected-objects.service";
import {TREE_EVENTS} from "angular-tree-component/dist/constants/events";

function isAllowCheck(node) {
  if (node.hasChildren) {
    if (node.getVisibleChildren().length == 0) {
      return false;
    }
    else {
      const visibleChildren = node.getVisibleChildren();

      // если хотя бы один checked есть в скрытых, то не разрешаем

      if (node.isSelected) {
        const children = node.children;

        for (let i = 0; i < children.length; i++) {
          if (children[i].isSelected && children[i].isHidden) {
            return false;
          }
          else if (children[i].isSelected) {
            return isAllowCheck(children[i]);
          }
        }
      }

      for (let i = 0; i < visibleChildren.length; i++) {
        if (isAllowCheck(visibleChildren[i])) {
          return true;
        }
      }

      return false;
    }
  }

  return true;
}

const actionMapping:IActionMapping = {
  mouse: {
    checkboxClick: (tree, node, $event) => {
      if (!isAllowCheck(node)) {
        $event.preventDefault();
      }
      else {
        if (node.isSelected) {
          TREE_ACTIONS.DESELECT(tree, node, $event);
        }
        else {
          TREE_ACTIONS.SELECT(tree, node, $event);
        }
      }
    }
  }
};

@Component({
  selector: 'app-protected-objects',
  templateUrl: './protected-objects.component.html',
  styleUrls: ['./protected-objects.component.scss']
})
export class ProtectedObjectsComponent implements OnInit {

  constructor(private rest: RestService, private protectedObjectService: ProtectedObjectsService) {
    this.savedState = [];
  }

  private savedState;

  @ViewChild(TreeComponent)
  private tree: TreeComponent;

  expandToLeaf(leafId, expandedNodeIds) {
    const node = this.tree.treeModel.getNodeById(leafId);

    this.getIdsFromLeafToRoot(node, expandedNodeIds);
  }

  getIdsFromLeafToRoot(node, expandedNodeIds) {
    if ((node["parent"] != null) && (node.isRoot == null || !node.isRoot)) {
      expandedNodeIds[node.parent.id] = true;

      this.getIdsFromLeafToRoot(node.parent, expandedNodeIds);
    }
  }

  processSavedState() {
    let protectedObjects = this.getSavedState();

    if (protectedObjects == null) {
      return;
    }

    let returnProtectedObjectsList = [];

    Object.keys(protectedObjects).map(function (objectKeyElement) {
      const mapKeys = protectedObjects[objectKeyElement];

      returnProtectedObjectsList.push(mapKeys);
    });

    let selectedLeafNodeIds = {};

    let expandedNodeIds = {};

    let activeNodeIds = {};

    let hiddenNodeIds = {};

    let focusedNodeId;

    for (let i = 0; i < returnProtectedObjectsList.length; i++) {
      let findedId = this.protectedObjectService.recursiveFindProtectedObject(returnProtectedObjectsList[i], this.tree.treeModel.nodes);

      if (findedId != -1) {
        selectedLeafNodeIds[findedId] = true;

        this.expandToLeaf(findedId, expandedNodeIds);

        this.tree.treeModel.setState({selectedLeafNodeIds,
          expandedNodeIds,
          activeNodeIds,
          hiddenNodeIds,
          focusedNodeId
        });
      }
    }
  }

  public setSavedState(savedState) {
    this.savedState = savedState;
  }

  public getSavedState() {
    return this.savedState;
  }

  onUpdateData(event) {
    this.processSavedState();
  }

  ngOnInit() {
    this.loadObjects();

    const el = document.getElementById('filter');

    const keyUps = fromEvent(el, 'keyup');

    const subscription = keyUps.pipe(debounceTime(500)).subscribe((evt: KeyboardEvent) => {
      this.filterFn((<HTMLInputElement>evt.target).value, this.tree.treeModel);
    });
  }

  loadObjects() {
    return this.rest.getObjects().subscribe((data: {}) => {
      const self = this;

      Object.keys(data).map(function(objectKeyElement) {
        const mapKeys = data[objectKeyElement];

        Object.keys(mapKeys).map(function(mapKeyElement) {
          const value = mapKeys[mapKeyElement];
          self.nodes.push(value);
        });
      });

      this.tree.treeModel.update();
    });
  }

  public getTreeComponent() {
    return this.tree;
  }

  public getNodes() {
    return this.nodes;
  }

  public clearNodes() {
    this.nodes = [];
  }

  nodes = [];

  private _filterNode(ids, node, filterFn, autoShow) {
    // if node passes function then it's visible
    let isVisible = filterFn(node);

    if (node.children && !isVisible) {
      // if one of node's children passes filter then this node is also visible
      node.children.forEach((child) => {
        if (this._filterNode(ids, child, filterFn, autoShow)) {
          isVisible = true;
        }
      });
    }

    // mark node as hidden
    if (!isVisible) {
      // if (node.isSelected) {
      //   node.setIsSelected(false);
      // }

      ids[node.id] = true;
    }
    // auto expand parents to make sure the filtered nodes are visible
    if (autoShow && isVisible) {
      node.ensureVisible();
    }
    return isVisible;
  }

  filterFn(value: string, treeModel: TreeModel) {
    const ids = {};

    this.tree.treeModel.roots.forEach((node) => this._filterNode(ids, node, (node: TreeNode) => {
      let dataWithName = "";

      if (node.data.hasOwnProperty("name")) {
        dataWithName = node.data.name + " (" + node.data.data + ")";
      }
      else {
        dataWithName = node.data.data;
      }

      return fuzzysearch(value, dataWithName);
    }, true));

    this.tree.treeModel.hiddenNodeIds = ids;
    this.tree.treeModel.fireEvent({ eventName: TREE_EVENTS.changeFilter });
  }

  options: ITreeOptions = {
    displayField: 'data',
    childrenField: 'children',
    useCheckbox: true,
    useVirtualScroll: true,
    nodeHeight: 22,
    actionMapping
  };
}

function fuzzysearch (needle: string, haystack: string) {
  const haystackLC = haystack.toLowerCase();
  const needleLC = needle.toLowerCase();

  const hlen = haystack.length;
  const nlen = needleLC.length;

  if (nlen > hlen) {
    return false;
  }
  if (nlen === hlen) {
    return needleLC === haystackLC;
  }
  outer: for (let i = 0, j = 0; i < nlen; i++) {
    const nch = needleLC.charCodeAt(i);

    while (j < hlen) {
      if (haystackLC.charCodeAt(j++) === nch) {
        continue outer;
      }
    }
    return false;
  }
  return true;
}