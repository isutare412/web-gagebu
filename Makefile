# Load environment variables from .env.local if it exists
ifneq (,$(wildcard .env.local))
    include .env.local
    export
endif

PLATFORM ?= linux/amd64
API_IMAGE ?= redshoore/webgagebu-api
API_TAG ?= latest
UI_IMAGE ?= redshoore/webgagebu-ui
UI_TAG ?= latest

##@ General

.PHONY: help
help: ## Display this help.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Build

.PHONY: api-build
api-build: ## Build the Docker image.
	docker build --build-arg PLATFORM=$(PLATFORM) -t $(API_IMAGE):$(API_TAG) ./api

.PHONY: api-push
api-push: ## Push the Docker image to the registry.
	echo $(DOCKER_PASSWORD) | docker login -u $(DOCKER_USER) --password-stdin
	docker push $(API_IMAGE):$(API_TAG)

.PHONY: ui-build
ui-build: ## Build the UI Docker image.
	docker build --build-arg PLATFORM=$(PLATFORM) -t $(UI_IMAGE):$(UI_TAG) ./ui

.PHONY: ui-push
ui-push: ## Push the UI Docker image to the registry.
	echo $(DOCKER_PASSWORD) | docker login -u $(DOCKER_USER) --password-stdin
	docker push $(UI_IMAGE):$(UI_TAG)
