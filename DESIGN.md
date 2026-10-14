---
name: "Estoque Inteligente"
description: "Wholesale catalog: cobalt navigation, precise stock tables and physical position labels."
colors:
  ground: "#f2f5fa"
  paper: "#fff"
  subtle: "#f7f9fc"
  ink: "#20304b"
  muted: "#58677d"
  rule: "#dce3ee"
  field-rule: "#9aa9be"
  accent: "#204fb0"
  accent-hover: "#173d8c"
  selected: "#e8efff"
  selected-ink: "#163f91"
  rail: "#204fb0"
  rail-ink: "#fff"
  rail-muted: "#dce6ff"
  danger: "#ac302d"
  danger-ground: "#fff0ee"
  success: "#226346"
  success-ground: "#eaf6ee"
  dark-ground: "#111a2a"
  dark-paper: "#1a2639"
  dark-subtle: "#202e44"
  dark-ink: "#eef3ff"
  dark-muted: "#afbdd2"
  dark-rule: "#35435b"
  dark-field-rule: "#697b96"
  dark-accent: "#b4ccff"
  dark-accent-hover: "#d5e2ff"
  dark-selected: "#273f6b"
  dark-selected-ink: "#e0ebff"
  dark-rail: "#173d8c"
  dark-danger: "#ffb6ae"
  dark-danger-ground: "#48282e"
  dark-success: "#a2e0ba"
  dark-success-ground: "#213c32"
typography:
  display:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: "clamp(2.7rem,4.7vw,4.8rem)"
    fontWeight: 700
    lineHeight: 1.06
    letterSpacing: "-.03em"
  headline:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: "clamp(1.8rem,2.5vw,2.5rem)"
    fontWeight: 700
    lineHeight: 1.15
    letterSpacing: "-.03em"
  title:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: "1.2rem"
    fontWeight: 700
    lineHeight: 1.3
    letterSpacing: "-.02em"
  body:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: "15px"
    fontWeight: 400
    lineHeight: 1.5
  label:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: ".875rem"
    fontWeight: 600
    lineHeight: 1.5
  location:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: "1.25rem"
    fontWeight: 750
    lineHeight: 1.5
  table:
    fontFamily: "\"Segoe UI Variable\",\"Segoe UI\",sans-serif"
    fontSize: ".875rem"
    fontWeight: 400
    lineHeight: 1.5
rounded:
  surface: "14px"
  control: "7px"
  badge: "5px"
spacing:
  xs: "8px"
  sm: "12px"
  md: "16px"
  lg: "24px"
  xl: "36px"
components:
  button-primary:
    backgroundColor: "{colors.accent}"
    textColor: "{colors.paper}"
    rounded: "{rounded.control}"
    padding: "10px 18px"
  button-primary-hover:
    backgroundColor: "{colors.accent-hover}"
    textColor: "{colors.paper}"
  button-secondary:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.accent}"
    rounded: "{rounded.control}"
    padding: "10px 18px"
  button-quiet:
    backgroundColor: "transparent"
    textColor: "{colors.accent}"
    padding: "6px 0"
  button-danger:
    backgroundColor: "{colors.danger-ground}"
    textColor: "{colors.danger}"
    rounded: "{rounded.control}"
    padding: "10px 18px"
  input:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.control}"
    padding: "10px 12px"
  navigation:
    textColor: "{colors.rail-muted}"
    rounded: "{rounded.control}"
    padding: "10px 12px"
  badge:
    backgroundColor: "{colors.selected}"
    textColor: "{colors.selected-ink}"
    rounded: "{rounded.badge}"
    padding: "4px 9px"
  card:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.surface}"
    padding: "24px"
  location-code:
    backgroundColor: "{colors.selected}"
    textColor: "{colors.selected-ink}"
    typography: "{typography.location}"
    rounded: "{rounded.control}"
    padding: "7px 13px"
---

# Design System: Estoque Inteligente

## Overview

**Creative North Star: "Wholesale catalog"**

Wholesale catalog connects product identity, stock and physical position through a solid cobalt navigation rail and calm working surfaces. Compact tables, explicit labels and humanist typography support repeated counter and warehouse tasks. The selected world replaces the former light-glass treatment completely.

Light is the default. Dark and system preferences preserve the same hierarchy and component geometry. Position labels supply the recurring visual signature; solid surfaces, precise rules and restrained movement keep operational content legible.

**Key Characteristics:**
- Solid cobalt navigation and cool neutral work areas.
- Compact tables with tabular quantities and distinct position labels.
- Humanist system typography, explicit labels and visible keyboard focus.
- Selectable light, dark and system themes; reduced-motion support.

## Colors

The palette combines a committed cobalt identity with cool paper, blue ink and quiet rules. Frontmatter owns the exact values extracted from app.css; dark-prefixed tokens are the overrides applied by the dark theme. Rail ink and rail muted inherit their light values in both modes.

### Primary

Cobalt accent defines actions, links and focus. Deep cobalt is its hover state. Cobalt rail is the persistent navigation identity; its darker night variant stays saturated. Selected blue and selected ink form the paired selection treatment.

### Neutral

Ground is the page canvas; paper is the working panel and field surface; subtle differentiates table headers and quiet regions. Ink carries primary content, muted carries secondary copy, rule divides surfaces and field-rule keeps controls recognizable. Rail ink and rail muted are dedicated text colors on cobalt.

### Semantic states

Danger and danger ground pair errors and destructive actions. Success and success ground pair confirmation notices. Dark overrides retain those meanings without reusing light-theme contrast assumptions.

**The Semantic Pair Rule.** Use foreground and background tokens together when themes change; retain the dedicated rail colors for navigation.

## Typography

The shipped display and body stack is Segoe UI Variable, Segoe UI, sans-serif. This humanist system stack serves the operational surface without external font assets. Frontmatter records the actual role values; compact tables and field labels use the same family.

Display is used by the login introduction; headline is the ordinary page heading; title is the shared section heading. Body uses a 1.5 line height and paragraphs stop at 72ch. Labels remain sentence case. Position codes are heavier and larger than labels; the inspector title uses 1.45rem and the stock total uses 1.6rem. At the narrow login breakpoint, the display becomes 2.6rem.

**The Quantity Rule.** Set quantities and position codes in tabular numerals; align numeric table columns to the right.

## Layout

The desktop shell has a 240px sticky rail and a flexible workspace. Main content is centered within 1680px, with 32px 36px 48px padding. The workspace bar separates context and theme choice from the page heading. Repeated panel gaps use 24px, panel padding uses 24px, and form stacks use 18px; the spacing frontmatter records reused steps, not a compulsory universal grid.

The catalog pairs its flexible table with a 300px sticky inspector; at 1700px and wider the inspector becomes 340px. At 1200px and below the rail becomes 210px, the inspector 270px and the catalog gap 18px; editor columns collapse. At 900px and below the rail becomes a top region with native collapsible navigation, and operation columns stack. At 700px and below catalog and login columns stack, the inspector stops sticking and main padding becomes 24px 18px 36px. At 380px and below navigation and paired form fields become single-column.

General tables retain a 520px minimum width in an overflow container; the inventory table deliberately permits a narrower fit. Desktop table cells use 16px padding, reducing to 14px 12px and then 13px 11px. The position grid uses auto-fill columns with a 230px floor and a narrow-screen fallback. Controls generally have a 44px minimum height; the compact theme select and navigation retain their observed dedicated sizes.

## Elevation & Depth

Solid fills and one-pixel rules establish working hierarchy. Panels have no resting shadow. Transient feedback alone uses the shadow token recorded in the sidecar; this is a status overlay, not a material applied to every card.

**The Flat Surface Rule.** Working panels use solid fills and fine borders. Reserve the shipped shadow for transient feedback.

## Shapes

Surface, control and badge radii are distinct frontmatter tokens. Working panels have soft but bounded corners; controls are tighter and badges tighter again. Tables use a rounded outer container and straight internal rules. Vacant position panels use a dashed border. Navigation icons are inline outline SVG with rounded joins; the brand uses a geometric box mark.

## Components

### Buttons

Primary buttons use accent with paper text, medium-heavy weight and the control radius. Hover uses accent-hover. Secondary buttons use paper, accent text and field-rule borders; hover adopts the selected pair. Quiet buttons are unfilled text actions with an underline on hover. Danger buttons pair the danger tokens and invert them on hover. Disabled buttons reduce opacity to .5; busy buttons use a progress cursor. No separate active animation is shipped.

### Inputs / Fields

Fields use paper, ink and a one-pixel field-rule border, with control corners. Visible labels sit above fields with a 7px gap. Read-only fields use subtle. Placeholder text uses muted at full opacity. Global keyboard focus is a 3px accent outline with a 3px offset; the rail uses white focus. Validation messages use the error treatment and explicit text.

### Navigation

The cobalt rail uses rail-muted links and rail-ink hover text over a translucent white tint. Active links are white with deep cobalt text and heavier weight, plus aria-current. Desktop links have a 42px minimum height. Below the navigation breakpoint, native details/summary provides the disclosure; app.js synchronizes its open state with the viewport. The active store remains in the workspace bar when the desktop store block is hidden.

### Chips / Tags

Badges are compact selected-blue labels with selected ink, badge corners and semibold text. They communicate context such as demonstration status and are not generic interactive filters.

### Cards / Containers

Editors and inspectors share paper, a rule border, surface corners and normal panel padding. Position drawers use the same vocabulary with 22px padding; empty drawers use subtle and a dashed rule. Data tables remain tables rather than an invented card grid.

### Position codes

The recurring position label pairs selected blue and selected ink with heavier tabular numbers, control corners and a 44px minimum height. Linked position codes switch to accent and paper on hover. Reuse this label wherever physical locations need quick recognition.

### Feedback and motion

Notices pair semantic color with explicit text. The fixed live message announces asynchronous status through a polite live region. Device actions expose busy state and distinguish command submission, confirmation and timeout. The inspector enters with a 5px vertical reveal over .25s using the sidecar easing. Button and navigation color changes take .16s. Reduced-motion preference removes animation and transitions globally. Theme choice defaults to light, supports dark/system and persists when local storage is available.

## Do's and Don'ts

### Do:
- Do use the semantic foreground/background pairs in both themes.
- Do retain explicit labels, visible focus and reduced-motion behavior.
- Do repeat position labels consistently across the inspector, map and assisted search.
- Do use inline SVG for the existing outline icon language.

### Don't:
- Don't restore translucent glass panels or backdrop blur.
- Don't turn compact working tables into decorative cards.
- Don't use color alone to communicate selection, errors or device confirmation.
- Don't depict demonstration data or simulator acknowledgements as real hardware telemetry.
