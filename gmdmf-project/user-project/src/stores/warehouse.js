import { defineStore } from "pinia";
import { ref } from "vue";

export const useWarehouseStore = defineStore(
  "warehouse",
  () => {
    const warehouse = ref(null);

    const setWarehouse = (newWarehouse) => {
      warehouse.value = newWarehouse;
    };

    const removeWarehouse = () => {
      warehouse.value = null;
    };

    return { warehouse, setWarehouse, removeWarehouse };
  },
  { persist: true }
);
