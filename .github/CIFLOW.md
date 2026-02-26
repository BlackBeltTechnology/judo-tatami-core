# Development Version and Branch Handling

## Table of Contents

- [Branches](#branches)
- [Version Numbers](#version-numbers)
  - [GitHub Action Flows](#github-action-flows)
    - [build.yml](#buildyml)
    - [merge-pr-tagged.yml](#merge-pr-taggedyml)
    - [create-release-on-master.yml](#create-release-on-masteryml)
    - [release.yml](#releaseyml)
- [How to Develop](#how-to-develop)

---

## Branches

The versioning policy of JUDO NG modules is based on [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

Branches:

- **develop** -- development branch containing the latest development sources of the last active version
- **feature/JNG-NUMBER_short_summary** -- feature branches are based on **develop** and contain sources of new features that will be included in the last active version
- **(release/)1_0_beta1** -- release branches of 1.0-beta1 (the `release/` prefix is still reserved for CI)
- **bugfix/JNG-NUMBER_short_summary**, **support/JNG-NUMBER_short_summary** -- bugfix and support branches are based on release branches and must be applied to release and development branches of newer versions too
- **master** -- contains the latest released sources of the last active version

The following diagram illustrates the branching model and how branches relate to each other over time:

```mermaid
flowchart LR
    subgraph master["master (chartreuse)"]
        direction LR
        m0(( )) --> m1(( )) --> m2(( )) --> m3(( ))
    end

    subgraph develop["develop (blue)"]
        direction LR
        d0(( )) --> d1(( )) --> d2(( )) --> d3(( )) --> d4(( )) --> d5(( )) --> d6(( )) --> d7(( ))
    end

    subgraph features["feature branches (gold)"]
        direction LR
        f1_0(("JNG-2")) --> f1_1(( )) --> f1_2(( ))
        f2_0(("JNG-1")) --> f2_1(( )) --> f2_2(( ))
        f3_0(("JNG-3")) --> f3_1(( ))
    end

    subgraph releases["release branches (cyan)"]
        direction LR
        r1_0(("1.0-beta1")) --> r1_1(( )) --> r1_2(( ))
        r2_0(("1.0-beta2")) --> r2_1(( )) --> r2_2(( )) --> r2_3(( ))
        r3_0(("1.1-beta1")) --> r3_1(( )) --> r3_2(( ))
    end

    subgraph bugfix["bugfix (red)"]
        direction LR
        b1_0(("JNG-4")) --> b1_1(( ))
    end

    subgraph support["support (aquamarine)"]
        direction LR
        s1_0(("JNG-5")) --> s1_1(( ))
    end

    subgraph hotfix["hotfix (red)"]
        direction LR
        h1_0(("JNG-6")) --> h1_1(( ))
    end

    %% Feature branches from/to develop
    d0 --> f1_0
    f1_2 --> d1
    d0 --> f2_0
    f2_2 --> d2
    d2 --> f3_0
    f3_1 --> d3

    %% Release 1.0-beta1 from develop
    d2 --> r1_0
    r1_0 --> b1_0
    b1_1 --> r1_2
    r1_2 --> d4

    %% Release 1.0-beta2 from develop
    d3 --> r2_0
    r2_0 --> s1_0
    s1_1 --> r2_1
    r1_2 --> r2_2
    r2_3 --> m1
    r2_3 --> d5

    %% Hotfix from master
    m1 --> h1_0
    h1_1 --> m2
    h1_1 --> d6
    h1_1 --> r3_1

    %% Release 1.1-beta1 from develop
    d5 --> r3_0
    r3_2 --> d7
    r3_2 --> m3

    %% Initial master to develop
    m0 --> d0

    style master fill:#7fff00,color:#000
    style develop fill:#6495ed,color:#fff
    style features fill:#ffd700,color:#000
    style releases fill:#00ffff,color:#000
    style bugfix fill:#ff6347,color:#fff
    style support fill:#7fffd4,color:#000
    style hotfix fill:#ff6347,color:#fff
```

---

## Version Numbers

Version numbers are managed using semantic versioning. The rules for when and how to change version numbers depend on the branch type:

| Branch Type | Version Rule |
|---|---|
| **feature/** | Do **not** change version numbers when starting feature branches |
| **develop** | 2nd number in version is increased when a release branch is started |
| **bugfix/** | Do **not** change version numbers on bugfix branches -- these are applied on release branches during testing before releasing (merging to master) |
| **support/** | 3rd number in version is increased when started -- used to support a previous release including new (minor) changes; support branches are merged back to the release branch when the update is released (without merging changes to master) |
| **hotfix/** | 4th number in version is increased when started -- these are applied on both release and master branches |

---

### GitHub Action Flows

#### build.yml

The `build.yml` workflow is the primary build pipeline. It is triggered on pushes to the **develop** branch or on pull requests targeting **develop**, **master**, **increment/\***, or **release/\*** branches. The version calculation strategy depends on the target branch.

```mermaid
flowchart TD
    A["<b>Trigger</b><br/>Push on <b>develop</b> branch<br/>or<br/>Pull request on <b>develop</b>, <b>master</b>,<br/><b>increment/*</b>, <b>release/*</b> branch"]
    A --> B{"Commit or PR<br/>base branch?"}

    B -->|"master, release/*"| C["Set <b>version</b><br/>from project <b>pom.xml</b><br/>(version without '-SNAPSHOT')"]
    B -->|"develop, increment/*"| D["Set <b>version</b><br/><i>major.minor.qualifier.date_commitId_branchName</i><br/>from project <b>pom.xml</b><br/>(version without '-SNAPSHOT')"]

    C --> E["Build and deploy to Nexus"]
    D --> E

    E --> F["Create git tag <b>v&lt;version&gt;</b>"]

    F --> G{"PR or commit<br/>base branch?"}
    G -->|"increment/*, release/*"| H["Create tag <b>merge-pr/&lt;version&gt;</b>"]
    H --> I["Triggers <b>merge-pr-tagged.yml</b>"]

    G -->|"develop"| J["Build change log"]
    J --> K["Create <b>GitHub release</b><br/>(prerelease) with change log"]

    G -->|"other"| L["End"]
    I --> L
    K --> L

    style A fill:#f5deb3,stroke:#000
    style I fill:#90ee90,stroke:#000
```

---

#### merge-pr-tagged.yml

The `merge-pr-tagged.yml` workflow is triggered when a tag matching `merge-pr/*` is pushed. It determines whether to merge the associated pull request to **master** or squash it to **develop** based on the version format.

```mermaid
flowchart TD
    A["<b>Trigger</b><br/>Push on <b>merge-pr/*</b> tag"]
    A --> B["Get <b>&lt;version&gt;</b> from tag name"]
    B --> C{"Check <b>&lt;version&gt;</b><br/>format"}

    C -->|"major.minor.qualifier"| D["Merge pull request to <b>master</b>"]
    D --> E["Triggers <b>create-release-on-master.yml</b>"]

    C -->|"other format"| F["Squash pull request to <b>develop</b>"]
    F --> G["Triggers <b>build.yml</b>"]

    E --> H["Delete tag <b>merge-pr/&lt;version&gt;</b>"]
    G --> H
    H --> I["End"]

    style A fill:#f5deb3,stroke:#000
    style E fill:#90ee90,stroke:#000
    style G fill:#90ee90,stroke:#000
```

---

#### create-release-on-master.yml

The `create-release-on-master.yml` workflow is triggered on any push to the **master** branch. It creates a GitHub release marked as the latest release, including a generated changelog.

```mermaid
flowchart TD
    A["<b>Trigger</b><br/>Push on <b>master</b> branch"]
    A --> B["Get <b>&lt;version&gt;</b> from tag name"]
    B --> C["Build change log"]
    C --> D["Create <b>GitHub release</b><br/>(latest) with change log"]
    D --> E["End"]

    style A fill:#f5deb3,stroke:#000
```

---

#### release.yml

The `release.yml` workflow is triggered manually with a **given version** parameter, which can be `'auto'` or any version in `major.minor.qualifier` form. It creates two pull requests: one targeting **master** with the release version and one targeting **develop** with the next incremented version.

```mermaid
flowchart TD
    A["<b>Trigger</b><br/>Manually triggered with <b>given version</b><br/>('auto' or <i>major.minor.qualifier</i>)"]
    A --> B{"<b>given version</b><br/>is?"}

    B -->|"'auto'"| C["Set <b>release version</b><br/>from project <b>pom.xml</b><br/>(version without '-SNAPSHOT')"]
    B -->|"other"| D["Set <b>release version</b><br/>to given <b>version</b>"]

    C --> E["Set <b>next version</b> to<br/><b>release version</b>'s qualifier + 1"]
    D --> E

    E --> F["Create PR on <b>master</b><br/>with <b>release version</b>"]
    F --> G["Triggers <b>build.yml</b>"]

    G --> H["Create PR on <b>develop</b><br/>with <b>next version</b>"]
    H --> I["Triggers <b>build.yml</b>"]
    I --> J["End"]

    style A fill:#f5deb3,stroke:#000
    style G fill:#90ee90,stroke:#000
    style I fill:#90ee90,stroke:#000
```

---

## How to Develop

For issue tracking we use [JIRA](https://blackbelt.atlassian.net/jira/dashboards). The golden rule:

> **There is no commit without ticket number.**

Every pull request or commit must include a `JNG-xxx` ticket reference.
