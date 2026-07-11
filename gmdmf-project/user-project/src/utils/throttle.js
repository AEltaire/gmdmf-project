// 节流
export function _throttle(fn, wait = 200) {
  let last, timer, now;
  return function () {
    now = Date.now();
    if (last && now - last < wait) {
      clearTimeout(timer);
      timer = setTimeout(function () {
        last = now;
        fn.call(this, ...arguments);
      }, wait);
    } else {
      last = now;
      fn.call(this, ...arguments);
    }
  };
}
