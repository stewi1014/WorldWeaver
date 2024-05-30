# Upgrading Minecraft Version

Use the following order to upgrade the single packages (based on inter-dependencies):

* **wover-common** (no dependency)
* **wover-core** (depends on _wover-common_)
* **wover-math** (depends on _wover-core_)
* **wover-datagen** (depends on _wover-core_)
* **wover-event** (depends on _wover-core_)
* **wover-ui** (depends on _wover-core_ and _wover-event_)
* **wover-tag** (depends on _wover-core_, _wover-datagen_ and _wover-event_)
* **wover-block** (depends on _wover-core_, _wover-tag_)
* **wover-preset** (depends on _wover-core_, _wover-tag_ and _wover-event_)
* **wover-surface** (depends on _wover-common-api_, _wover-datagen-api_, _wover-core-api_, _wover-math-api_ and
  _wover-event-api_)
* **wover-structure** (depends on _wover-core-api_, _wover-math-api_, _wover-event-api_ and _wover-block-api_)
* **wover-feature** (depends on _wover-core-api_, _wover-event-api_, _wover-surface-api_, _wover-block-api_ and
  _wover-structure-api_)
* **wover-biome** (depends on _wover-core-api_, _wover-event-api_ and _wover-feature-api_)
* **wover-generator** (depends on _wover-core-api_, _wover-event-api_, _wover-surface-api_, _wover-biome-api_,
  _wover-preset-api_, _wover-ui-api_ and _wover-tag-api_)