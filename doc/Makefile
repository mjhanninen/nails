HTML_DIR=html
PRODUCT_DIRS=$(HTML_DIR)

# Markdown documents
MD_CMD=pandoc --from markdown --to html --standalone
MD_PRODUCTS=$(patsubst %.md,$(HTML_DIR)/%.html,$(wildcard *.md))

.PHONY: all clean dirs help html markdown

all: html

help:
	@echo "Please use \`make <target>\` where <target> is one of"
	@echo "  all    to make all products"
	@echo "  html   to generate HTML documentation"
	@echo "  clean  to clean up project directories"

html: markdown

markdown: dirs $(MD_PRODUCTS)

$(HTML_DIR)/%.html: %.md
	$(MD_CMD) $< --output $@

dirs:
	@for d in $(PRODUCT_DIRS); do mkdir -p $$d; done

clean:
	rm -fv $(MD_PRODUCTS)
	-rm -dv $(PRODUCT_DIRS)
