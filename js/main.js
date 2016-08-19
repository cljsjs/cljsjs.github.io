// source: https://davidwalsh.name/javascript-debounce-function
// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
function debounce(func, wait, immediate) {
	var timeout;
	return function() {
		var context = this, args = arguments;
		var later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
};

var show = function (el) {
    $(el).css("display", "");
};

var hide = function (el) {
    $(el).css("display", "none");
};

// An array to hold filter information
var packages = [];

var filterPackages = function (evt) {
  var term = evt.target.value;

  // Fast show all when input is blank
  if (term === '') {
    // Show all packages
    $('#currently-available > ul > li').css('display', '');
    return;
  }
  // At least two chars required
  if (term.length < 2) return;

  packages.forEach(function(item) {
    item.text.match(term) ? show(item.element) : hide(item.element);
  });
};

$(function() {
  // preload packages text for search
  $('#currently-available > ul > li').each(function(index, el) {
    packages.push({text: el.innerText, element: el});
  });

  var client = new ZeroClipboard($('#currently-available span.clojars button'));

  client.on('aftercopy', function(e) {
    e.target.innerHTML = "Copied";
  });

  $('#currently-available span.clojars input').attr('readonly', 'readonly');

  $(function(){
    $(document).on('click','input[type=text]',function(){ this.select(); });
    $(document).on('keyup', 'input#package-filter', debounce(filterPackages, 300));
  });
});
