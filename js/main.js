var show = function (el) {
    $(el).css("display", "");
};

var hide = function (el) {
    $(el).css("display", "none");
};

var filterPackages = function (term) {
    $('#currently-available > ul > li').each(function(index, el) {
        el.innerText.match(term) ? show(el) : hide(el);
    });
};

$(function() {
  var client = new ZeroClipboard($('#currently-available span.clojars button'));

  client.on('aftercopy', function(e) {
    e.target.innerHTML = "Copied";
  });

  $('#currently-available span.clojars input').attr('readonly', 'readonly');

  $(function(){
    $(document).on('click','input[type=text]',function(){ this.select(); });

      $(document).on('keyup', 'input#package-filter', function(evt) {
          filterPackages(evt.target.value);
      });
  });
});
