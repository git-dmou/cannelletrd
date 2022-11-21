(function ($) {

  $.fn.fontselect = function (options) {

    var __bind = function (fn, me) { return function () { return fn.apply(me, arguments); }; };

    var settings = {
      style: 'selectfont',
      placeholder: 'Font',
      lookahead: 2,
      api: 'https://fonts.googleapis.com/css2?family=',
      fonts: [
        "Abel",
        "Acme",
        "Allan",
        "Alegreya",
        "Amaranth",
        "Annie+Use+Your+Telescope",
        "Anonymous+Pro",
        "Allerta+Stencil",
        "Amita",
        "Anton",
        "Architects+Daughter",
        "Arimo",
        "Artifika",
        "Asset",
        "Astloch",
        "Balsamiq+Sans",
        "Bangers",
        "Barlow",
        "Bentham",
        "Bevan",
        "Brawler",
        "Bree+Serif",
        "Buda",
        "Cabin",
        "Cabin+Sketch",
        "Calligraffitti",
        "Candal",
        "Cantarell",
        "Cardo",
        "Carter+One",
        "Cera",
        "Chewy",
        "Coda",
        "Coming+Soon",
        "Comfortaa",
        "Copse",
        "Corben",
        "Cousine",
        "Courgette",
        "Covered+By+Your+Grace",
        "Crafty+Girls",
        "Crimson+Text",
        "Crushed",
        "Cuprum",
        "Damion",
        "Didact+Gothic",
        "Droid+Sans",
        "Droid+Sans+Mono",
        "Droid+Serif",
        "EB+Garamond",
        "Expletus+Sans",
        "Fontdiner+Swanky",
        "Forum",
        "Fredoka+One",
        "Francois+One",
        "Fugaz+One",
        "Geo",
        "Give+You+Glory",
        "Goblin+One",
        "Goudy+Bookletter+1911",
        "Gravitas+One",
        "Gruppo",
        "Hammersmith+One",
        "Heebo",
        "Hind",
        "Holtwood+One+SC",
        "Homemade+Apple",
        "Inconsolata",
        "Indie+Flower",
        "Inter",
        "IM+Fell+DW+Pica",
        "Irish+Grover",
        "Irish+Growler",
        "Josefin+Sans",
        "Josefin+Slab",
        "Jua",
        "Kameron",
        "Karla",
        "Kenia",
        "Kranky",
        "Khula",
        "Kreon",
        "Kristi",
        "Krub",
        "La+Belle+Aurore",
        "Lexend+Deca",
        "Lato",
        "League+Script",
        "Lekton",
        "Limelight",
        "Lilita+One",
        "Lobster",
        "Lobster Two",
        "Lora",
        "Mako",
        "Mandali",
        "Maven+Pro",
        "Meddon",
        "MedievalSharp",
        "Megrim",
        "Merriweather",
        "Metrophobic",
        "Michroma",
        "Miltonian Tattoo",
        "Miltonian",
        "Modern Antiqua",
        "Modak",
        "Monofett",
        "Montserrat",
        "Molengo",
        "Mountains of Christmas",
        "Muli",
        "Neucha",
        "Neuton",
        "News+Cycle",
        "Nixie+One",
        "Nobile",
        "Nova+Cut",
        "Nova+Script",
        "Nunito",
        "Open+Sans",
        "Open+Sans:bold",
        "Open+Sans+Condensed",
        "Orbitron",
        "Oswald",
        "Over+the+Rainbow",
        "Reenie+Beanie",
        "Pacifico",
        "Patrick+Hand",
        "Parisienne",
        "Passion+One",
        "Playfair+Display",
        "Poppins",
        "Podkova",
        "Piazzolla",
        "PT+Sans",
        "PT+Mono",
        "PT+Sans+Narrow",
        "PT+Sans+Narrow:regular,bold",
        "PT+Serif",
        "PT+Serif Caption",
        "Puritan",
        "Quattrocento",
        "Quicksand",
        "Quattrocento+Sans",
        "Radley",
        "Raleway",
        "Redressed",
        "Reem+Kufi",
        "Roboto",
        "Roboto+Mono",
        "Rozha+One",
        "Rubik",
        "Satisfy",
        "Six+Caps",
        "Sacramento",
        "Slackey",
        "Secular+One",
        "Sniglet",
        "Special+Elite",
        "Stardos+Stencil",
        "Sue+Ellen+Francisco",
        "Suez+One",
        "Source+Sans+Pro",
        "Sunshiney",
        "Swanky+and+Moo+Moo",
        "Syncopate",
        "Tangerine",
        "Tenor+Sans",
        "Terminal+Dosis+Light",
        "The+Girl+Next+Door",
        "Tinos",
        "Ubuntu",
        "Unkempt",
        "UnifrakturCook",
        "UnifrakturMaguntia",
        "Varela",
        "Varela+Round",
        "Viga",
        "VT323",
        "Vollkorn",
        "Waiting+for+the+Sunrise",
        "Wallpoet",
        "Walter+Turncoat",
        "Wire+One",
        "Work+Sans",
        "Yanone+Kaffeesatz",
        "Yeseva+One",
        "Zeyada",
        "Zilla+Slab+Highlight",
        "Zilla+Slab+Highlight:bold"
      ]
    };

    var Fontselect = (function () {

      function Fontselect(original, o) {
        this.$original = $(original);
        this.options = o;
        this.active = false;
        this.setupHtml();
        this.getVisibleFonts();
        this.bindEvents();

        var font = this.$original.val();
        if (font) {
          this.updateSelected();
          this.addFontLink(font);
        }
      }

      Fontselect.prototype.bindEvents = function () {
        var self = this;
        // Close dropdown automatically on clicks outside dropdown
        $(document).click(function (event) {
          if (self.active && !$(event.target).parents('#fontSelect-' + self.$original.id).length) {
            self.toggleDrop();
          }
        });

        $('li', this.$results)
          .click(__bind(this.selectFont, this))
          .mouseenter(__bind(this.activateFont, this))
          .mouseleave(__bind(this.deactivateFont, this));

        $('span', this.$select).click(__bind(this.toggleDrop, this));
        this.$arrow.click(__bind(this.toggleDrop, this));
      };

      Fontselect.prototype.toggleDrop = function (ev) {

        if (this.active) {
          this.$element.removeClass('selectfont-active');
          this.$drop.hide();
          clearInterval(this.visibleInterval);
        } else {
          this.$element.addClass('selectfont-active');
          this.$drop.show();
          this.moveToSelected();
          this.visibleInterval = setInterval(__bind(this.getVisibleFonts, this), 500);
        }

        this.active = !this.active;
      };

      Fontselect.prototype.selectFont = function () {

        var font = $('li.active', this.$results).data('value');
        this.$original.val(font).change();
        this.updateSelected();
        this.toggleDrop();
      };

      Fontselect.prototype.moveToSelected = function () {

        var $li, font = this.$original.val();

        if (font) {
          $li = $("li[data-value='" + font + "']", this.$results);
        } else {
          $li = $("li", this.$results).first();
        }

        this.$results.scrollTop($li.addClass('active')[0].offsetTop);
      };

      Fontselect.prototype.activateFont = function (ev) {
        $('li.active', this.$results).removeClass('active');
        $(ev.currentTarget).addClass('active');
      };

      Fontselect.prototype.deactivateFont = function (ev) {

        $(ev.currentTarget).removeClass('active');
      };

      Fontselect.prototype.updateSelected = function () {

        var font = this.$original.val();
        $('span', this.$element).text(this.toReadable(font)).css(this.toStyle(font));
      };

      Fontselect.prototype.setupHtml = function () {

        this.$original.empty().hide();
        this.$element = $('<div>', { 'id': 'fontSelect-' + this.$original.id, 'class': this.options.style });
        this.$arrow = $('<div><b></b></div>');
        this.$select = $('<a><span>' + this.options.placeholder + '</span></a>');
        this.$drop = $('<div>', { 'class': 'my-drop' });
        this.$results = $('<ul>', { 'class': 'dp-show' });
        this.$original.after(this.$element.append(this.$select.append(this.$arrow)).append(this.$drop));
        this.$drop.append(this.$results.append(this.fontsAsHtml())).hide();
      };

      Fontselect.prototype.fontsAsHtml = function () {

        var l = this.options.fonts.length;
        var r, s, h = '';

        for (var i = 0; i < l; i++) {
          r = this.toReadable(this.options.fonts[i]);
          s = this.toStyle(this.options.fonts[i]);
          h += '<li data-value="' + this.options.fonts[i] + '" style="font-family: ' + s['font-family'] + '; font-weight: ' + s['font-weight'] + '">' + r + '</li>';
        }

        return h;
      };

      Fontselect.prototype.toReadable = function (font) {
        return font.replace(/[\+|:]/g, ' ');
      };

      Fontselect.prototype.toStyle = function (font) {
        var t = font.split(':');
        return { 'font-family': this.toReadable(t[0]), 'font-weight': (t[1] || 400) };
      };

      Fontselect.prototype.getVisibleFonts = function () {

        if (this.$results.is(':hidden')) return;

        var fs = this;
        var top = this.$results.scrollTop();
        var bottom = top + this.$results.height();

        if (this.options.lookahead) {
          var li = $('li', this.$results).first().height();
          bottom += li * this.options.lookahead;
        }

        $('li', this.$results).each(function () {

          var ft = $(this).position().top + top;
          var fb = ft + $(this).height();

          if ((fb >= top) && (ft <= bottom)) {
            var font = $(this).data('value');
            fs.addFontLink(font);
          }

        });
      };

      Fontselect.prototype.addFontLink = function (font) {

        let link = '<link href="' + this.options.api + font + '" rel="stylesheet" type="text/css">';


        let preview = $('#preview').contents();
        let screenPreview = preview.find('iframe').contents();

        if (preview.find("link[href*='" + font + "']").length === 0) {
          preview.find('link:last').after(link);
        }

        if (screenPreview.find("link[href*='" + font + "']").length === 0) {
          screenPreview.find('link:last').after(link);
        }

        if ($("link[href*='" + font + "']").length === 0) {
          $('link:last').after(link);
        }

      };

      return Fontselect;
    })();

    return this.each(function () {
      // If options exist, lets merge them
      if (options) $.extend(settings, options);

      return new Fontselect(this, settings);
    });

  };
})(jQuery);