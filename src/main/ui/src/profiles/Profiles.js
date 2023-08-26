import './Profiles.css'
import {useEffect, useState} from "react";

function Profile({profile}) {
  return (
      <div className='Profile'>
        <h3>{profile.name}</h3>
      </div>
  )
}

function Profiles() {
  const [profiles, setProfiles] = useState([]);
  useEffect(() => {
    fetch("/profiles")
        .then(resp => {
          if (resp.ok) {
            return resp.json()
          }
          else {
            throw new Error(resp);
          }
        })
        .then(resp => setProfiles(resp.data))
        .catch(reason => console.error(reason))
  }, []);

  return (
      <div>
        <h2>
          Profiles
        </h2>
        {
          profiles
              .sort((a, b) => a.name.localeCompare(b.name, [], {sensitivity: 'base'}))
              .map(value => <Profile id={value.id} profile={value}/> )
        }
      </div>
  )
}

export default Profiles;