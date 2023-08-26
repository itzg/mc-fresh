import {useEffect, useState} from "react";
import './Mods.css'

/**
 *
 * @param mod me.itzg.mcfresh.profiles.model.ProfileMod
 */
function Mod({profileId, mod, handleFetchError, handleUpgrade, handleToggleEnabled}) {
  const [availableVersion, setAvailableVersion] = useState(null);
  const [buttonDisabled, setButtonDisabled] = useState(false);

  useEffect(() => {
    fetch(`/profiles/${profileId}/mods/${mod.id}/availableUpgrade`)
        .then(resp => resp.ok ? resp.json() : handleFetchError(resp))
        .then(json => {
          if (json && json.status === 'hasUpgrade') {
            setAvailableVersion({
              version: json.version,
              details: json.details
            })
          }
        })
  }, [profileId, mod, handleFetchError]);

  function upgrade() {
    setButtonDisabled(true);
    handleUpgrade(mod, availableVersion.details);
  }

  return (
      <div className='Mod'>
        <div className='cell'>
          <input type='checkbox' checked={mod.enabled} onChange={() => handleToggleEnabled(mod)}></input>
        </div>
        <div className='cell'>
          {mod.name} (
          {mod.details.version}
          {availableVersion ? <button className='hasUpgrade' onClick={upgrade} disabled={buttonDisabled}>upgrade to {availableVersion.version}</button> : ""}
          )
        </div>
      </div>
  )
}

export default function Mods({profileId, handleFetchError}) {
  const [expanded, setExpanded] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [mods, setMods] = useState([]);

  function fetchMods() {
    fetch(`/profiles/${profileId}/mods`)
        .then(resp => {
          return resp.ok ? resp.json() : handleFetchError(resp);
        })
        .then(json => {
          setMods(json.data);
          setLoaded(true);
        })
  }

  useEffect(fetchMods, [profileId, handleFetchError]);

  function handleUpgrade(mod, details) {
      fetch(`/profiles/${profileId}/mods/${mod.id}/upgrade`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(details)
      })
          .then(resp => fetchMods())
  }

  function handleToggleEnabled(mod) {
    fetch(`/profiles/${profileId}/mods/${mod.id}/${mod.enabled ? 'disable' : 'enable'}`, {
      method: 'POST'
    })
        .then(resp => fetchMods())
  }

  function collapse() {
    setExpanded(false);
  }
  function expand() {
    setExpanded(true);
  }

  function countEnabledMods() {
    return mods.filter(value => value.enabled).length
  }

  return loaded && mods ? (<div>
        <h4>
          Mods ({countEnabledMods()} enabled) {
            expanded ? <button onClick={collapse}>Collapse</button> : <button onClick={expand}>Expand</button>
        }
        </h4>
        {
          expanded && mods
              // sort enabled ones first, then by name
              .sort((a, b) => a.enabled && b.enabled ?
                  a.name.localeCompare(b.name, [], {sensitivity: 'base'})
                : a.enabled ? -1 : 1
              )
              .map(mod => <Mod profileId={profileId} key={mod.id} mod={mod}
                               handleFetchError={handleFetchError}
                               handleUpgrade={handleUpgrade}
                               handleToggleEnabled={handleToggleEnabled}
              />)
        }
      </div>)
      : <div></div>;
}

