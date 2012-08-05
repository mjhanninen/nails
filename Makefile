HTML_DIR=doc/html
PRODUCT_DIRS=$(HTML_DIR)

# Markdown documents
MD_CMD=pandoc --from markdown --to html --standalone
MD_PRODUCTS=$(patsubst %.md,$(HTML_DIR)/%.html,$(wildcard *.md))

.PHONY: all dirs clean etags help html markdown tags

help:
	@echo "Please use \`make <target>\` where <target> is one of"
	@echo "  all    to make all products"
	@echo "  tags   to generate TAGS file"
	@echo "  html   to generate HTML documentation"
	@echo "  clean  to clean up project directories"

all: tags markdown

tags: etags

etags:
	test -e TAGS && rm TAGS || true
	find . -name "*.clj" -and -type f -and -not -empty -exec \
	     etags --append --language=none \
	           --regex='/(def[a-z]*-?[ \t\n]+\([^^][^ \t\n]*\)/\1/m' \
	           --regex='/(def[a-z]*-?[ \t\n]+^:[^ \t\n]*[ \t\n]+\([^ \t\n]+\)/\1/m' \
	           --regex='/(def[a-z]*-?[ \t\n]+^{[^}]*}[ \t\n]+\([^ \t\n]+\)/\1/m' \
	           {} \;

html: markdown
	make -C doc html

markdown: dirs $(MD_PRODUCTS)

$(HTML_DIR)/%.html: %.md
	$(MD_CMD) $< --output $@

dirs:
	@for d in $(PRODUCT_DIRS); do mkdir -p $$d; done

clean:
	make -C doc clean
	rm -fv TAGS
	rm -fv $(MD_PRODUCTS)
	-rm -dv $(PRODUCT_DIRS)
