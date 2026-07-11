$(
  (function ($) {

    // 便捷创建，插件导入即用，如需使用自定义插件、删除即可
    $(window).ready(function () {
      // 创建按钮
      $.initButton();
      // 创建笔记
      $.initNotePaper();
    });

    // 初始化笔记
    $.initNotePaper = function () {
      // 初始化笔记纸
      $("body").append(
        `<div class="note"><div class="note-paper-curr" style="top: 0; left: 0;">1</div><div class="note-paper-next" style="top: 20px; left: 20px;">2</div></div>`
      );
    };

    // 初始化按钮
    $.initButton = function (total) {
      $("body").append(
        `<button class="prevPaper">上一页</button><span class="curr" style="margin: 5px">1</span>/<span class="total" style="margin: 5px">${total ? total : 5
        }</span><button class="nextPaper">下一页</button><p>`
      );
      $('.prevPaper').prevButton()
      $('.nextPaper').nextButton()
    };

    // 下一页
    $.fn.nextButton = function () {
      const $btn = $(this)
      $btn.click(function () {
        // 页码设置
        if (parseInt($(".curr").text()) < parseInt($(".total").text())) {
          $(".curr").text(parseInt($(".curr").text()) + 1);
          $(".note-paper-prev").stop(true, true);
          $(".note-paper-curr").stop(true, false);
          $(".note-paper-next").stop(true, false);
        } else return;

        $(".note").createPaper();
        $(".note-paper-curr")
          .addClass("note-paper-prev")
          .removeClass("note-paper-curr");
        $(".note-paper-next")
          .addClass("note-paper-curr")
          .removeClass("note-paper-next");
        $(".note-paper-new")
          .addClass("note-paper-next")
          .removeClass("note-paper-new");

        // 动画
        $(".note-paper-prev").animate(
          {
            top: -20,
            left: -20,
            opacity: 0,
          },
          400,
          "linear",
          function () {
            $(".note-paper-prev").remove();
          }
        );

        $(".note-paper-curr").animate(
          {
            top: 0,
            left: 0,
            opacity: 1,
          },
          500,
          "linear"
        );

        $(".note-paper-next")
          .css({
            top: 40,
            left: 40,
            opacity: 0,
          })
          .animate(
            {
              top: 20,
              left: 20,
              opacity: 0.4,
            },
            800,
            "linear"
          );
      });
    }

    // 上一页
    $.fn.prevButton = function () {
      const $btn = $(this)
      $btn.click(function () {
        // 页码设置
        if (parseInt($(".curr").text()) > 1) {
          $(".curr").text(parseInt($(".curr").text()) - 1);
          $(".note-paper-prev").stop(true, true);
          $(".note-paper-curr").stop(true, true);
          $(".note-paper-next").stop(true, true);
        } else return;

        $(".note").createPaper();
        $(".note-paper-next")
          .addClass("note-paper-prev")
          .removeClass("note-paper-next");
        $(".note-paper-curr")
          .addClass("note-paper-next")
          .removeClass("note-paper-curr");
        $(".note-paper-new")
          .addClass("note-paper-curr")
          .removeClass("note-paper-new");

        // // 动画
        $(".note-paper-prev").animate(
          {
            top: 40,
            left: 40,
            opacity: 0,
          },
          400,
          "linear",
          function () {
            $(".note-paper-prev").remove();
          }
        );

        $(".note-paper-curr")
          .css({
            top: -20,
            left: -20,
            opacity: 0,
          })
          .animate(
            {
              top: 0,
              left: 0,
              opacity: 1,
            },
            500,
            "linear"
          );

        $(".note-paper-next").animate(
          {
            top: 20,
            left: 20,
            opacity: 0.4,
          },
          800,
          "linear"
        );
      });
    };

    // 创建笔记卡片
    $.fn.createPaper = function () {
      const $box = $(this);
      const $notePaper = $("<div></div>");
      $box.append($notePaper);
      $notePaper.addClass("note-paper-new");
      return $box;
    };
  })(jQuery)
);
