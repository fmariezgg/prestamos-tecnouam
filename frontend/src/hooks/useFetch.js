import { useEffect, useState } from 'react';
import { parseError } from '../utils/errors';

// Loads data with `fetcher` on mount and whenever reload() is called.
// `silent` reloads (after a mutation) keep the table visible instead of showing the spinner.
export default function useFetch(fetcher) {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [version, setVersion] = useState(0);

    useEffect(() => {
        let cancelled = false;
        fetcher()
            .then((result) => {
                if (cancelled) return;
                setData(result);
                setError(null);
            })
            .catch((e) => {
                if (!cancelled) setError(parseError(e));
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });
        return () => {
            cancelled = true;
        };
    }, [fetcher, version]);

    const reload = ({ silent = false } = {}) => {
        if (!silent) setLoading(true);
        setVersion((v) => v + 1);
    };

    return { data, loading, error, setError, reload };
}
