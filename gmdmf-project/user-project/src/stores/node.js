import { defineStore } from "pinia";
import { ref } from "vue";

export const useNodeStore = defineStore(
  "node",
  () => {
    const node = ref({
      node: null,
      index: null,
      i: null
    });

    const setNode = (newNode) => {
      node.value = newNode;
    };

    const removeNode = () => {
      node.value = {
        node: null,
        index: null,
        i: null
      };
    };

    return { node, setNode, removeNode };
  },
  { persist: true }
);
