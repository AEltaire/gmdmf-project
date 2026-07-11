import { createStore } from 'vuex'

const store = createStore({
  state() {
    return {
      // 通过 state 访问，store.state.count
      isLightTheme: true
    }
  },
  mutations: {
    // 通过 commit 调用，store.commit('increment')
    changeTheme(state) {
      state.isLightTheme = !state.isLightTheme
    }
  }
})

export default store