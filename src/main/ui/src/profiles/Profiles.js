import './Profiles.css'
import {useEffect, useState} from "react";
import Mods from "./Mods";

function Profile({profile, handleFetchError}) {
  return (
      <div className='Profile'>
        <h3>{profile.name}</h3>
        {
          profile.modded && <Mods profileId={profile.id} handleFetchError={handleFetchError}/>
        }
      </div>
  )
}

function Profiles() {
  const [err, setErr] = useState("");

  /**
   *
   * @param resp {Response} spring boot resp
   */
  function handleFetchError(resp) {
    resp.json()
        .then(bootErrJson => setErr(JSON.stringify(bootErrJson)))
  }

  const [profiles, setProfiles] = useState([]);
  useEffect(() => {
    fetch("/profiles")
        .then(resp => {
          return resp.ok ? resp.json() : handleFetchError(resp)
        })
        .then(resp => setProfiles(resp.data),
            reason => setErr(reason)
            )
  }, []);

  return (
      <div>
        <h2>
          Profiles
        </h2>
        {
            err && <div>ERROR: {err}</div>
        }
        {
          profiles
              .sort((a, b) => a.name.localeCompare(b.name, [], {sensitivity: 'base'}))
              .map(value => <Profile key={value.id} profile={value} handleFetchError={handleFetchError}/> )
        }
      </div>
  )
}

export default Profiles;